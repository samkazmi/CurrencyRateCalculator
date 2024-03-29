package com.app.currencyconverter.datasource.usecase

import androidx.lifecycle.LiveData
import androidx.paging.*
import androidx.work.*
import com.app.currencyconverter.datasource.local.entity.CurrencyRateEntity
import com.app.currencyconverter.datasource.models.ConversionRates
import com.app.currencyconverter.datasource.repository.CurrencyRepository
import com.app.currencyconverter.datasource.worker.SyncDataWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.concurrent.TimeUnit

class CurrencyConversionUsecaseImp constructor(
    private val repository: CurrencyRepository,
    private val workManager: WorkManager
) : BaseUsecase(), CurrencyConversionUsecase {

    companion object {
        const val SYNC_WORK = "SyncDataWork"
        const val SYNC_WORKER = "SyncDataWorker"
    }

    override fun getRates(): Flow<PagingData<CurrencyRateEntity>> {
        return Pager(config = PagingConfig(pageSize = 20), null) {
            repository.getCurrencyRatesFromDbPaged()
        }.flow
    }

    override fun getRates(
        selectedAmount: Double,
        selectedCurrency: CurrencyRateEntity
    ): Flow<PagingData<ConversionRates>> {
        return getRates().map { paging ->
            paging.filter {
                it.code != selectedCurrency.code
            }.map {
                it.toConversionRate(selectedAmount, selectedCurrency)
            }
        }.cachedIn(scope)
    }

    override fun getCurrencyList() = repository.getCurrencyList()
    override suspend fun getCurrencyListCount() = repository.getCurrencyListCount()
    override suspend fun getRatesCount() = repository.getRatesCount()

    override fun loadAndSaveCurrencyList() = repository.loadAndSaveCurrencyList()

    override suspend fun findCurrencyRate(currencyCode: String) =
        repository.findCurrencyRate(currencyCode)

    override fun updateSyncConversionRates() {
        startSyncConversionRates(ExistingPeriodicWorkPolicy.KEEP)
    }

    override fun forceSyncConversionRates() {
        startSyncConversionRates(ExistingPeriodicWorkPolicy.REPLACE)
    }

    private fun startSyncConversionRates(policy: ExistingPeriodicWorkPolicy) {
        workManager.enqueueUniquePeriodicWork(
            SYNC_WORK,
            policy,
            PeriodicWorkRequest.Builder(SyncDataWorker::class.java, 30, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .addTag(SYNC_WORKER)
                .build()
        )
    }

    override fun getSyncDataWorkerInfo(): LiveData<List<WorkInfo>> {
        return workManager.getWorkInfosForUniqueWorkLiveData(SYNC_WORK)
    }


}