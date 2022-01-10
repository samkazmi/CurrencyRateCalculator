package com.app.digitifysample

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.paging.PagingData
import androidx.work.Data
import androidx.work.WorkInfo
import com.app.digitifysample.datasource.local.entity.CurrencyEntity
import com.app.digitifysample.datasource.local.entity.CurrencyRateEntity
import com.app.digitifysample.datasource.models.ConversionRates
import com.app.digitifysample.datasource.remote.common.LiveResponse
import com.app.digitifysample.datasource.remote.common.Message
import com.app.digitifysample.datasource.usecase.CurrencyConversionUsecase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.*

class FakeCurrencyConversionUsecase : CurrencyConversionUsecase {

    val dummyRateList = mutableListOf<CurrencyRateEntity>()
    val dummyCurrencyList = mutableListOf<CurrencyEntity>()


    val dummyCurrencyEntityList = MutableLiveData<List<CurrencyEntity>>(dummyCurrencyList)
    val dummyWorkerLiveData = MutableLiveData<List<WorkInfo>>(listOf())

    private val enqueuedWorkInfo = WorkInfo(
        UUID.randomUUID(),
        WorkInfo.State.ENQUEUED,
        Data.EMPTY,
        listOf(),
        Data.EMPTY,
        1
    )
    private val runningWorkInfo = WorkInfo(
        UUID.randomUUID(),
        WorkInfo.State.RUNNING,
        Data.EMPTY,
        listOf(),
        Data.EMPTY,
        1
    )

    override fun getRates(): Flow<PagingData<CurrencyRateEntity>> {
        return flow {
            emit(PagingData.from(dummyRateList))
        }
    }

    override fun getRates(
        selectedAmount: Double,
        selectedCurrency: CurrencyRateEntity
    ): Flow<PagingData<ConversionRates>> {
        return flow {
            emit(PagingData.from(dummyRateList.filter {
                it.code != selectedCurrency.code
            }.map {
                it.toConversionRate(selectedAmount, selectedCurrency)
            }))
        }
    }

    override fun getCurrencyList(): LiveData<List<CurrencyEntity>> {
        return dummyCurrencyEntityList
    }

    override suspend fun getCurrencyListCount() = dummyCurrencyList.count()


    override suspend fun getRatesCount() = dummyRateList.count()


    override fun loadAndSaveCurrencyList(): LiveData<LiveResponse<Message>> {
        dummyWorkerLiveData.postValue(listOf())
        dummyCurrencyList.add(CurrencyEntity("AED", ""))
        dummyCurrencyList.add(CurrencyEntity("PKR", ""))
        dummyCurrencyList.add(CurrencyEntity("USD", ""))
        dummyCurrencyList.add(CurrencyEntity("SAR", ""))
        dummyCurrencyEntityList.postValue(dummyCurrencyList)
        return liveData {
            emit(LiveResponse.success(Message(200, "Success")))
        }
    }

    override fun onDestroy() {

    }

    override suspend fun findCurrencyRate(currencyCode: String): CurrencyRateEntity? {
        return dummyRateList.find { it.code == currencyCode }
    }


    override fun startSyncConversionRates() {
        dummyWorkerLiveData.postValue(listOf())
        dummyWorkerLiveData.postValue(listOf(enqueuedWorkInfo))
        dummyWorkerLiveData.postValue(listOf(runningWorkInfo))

        dummyRateList.add(CurrencyRateEntity("AED", 3.675))
        dummyRateList.add(CurrencyRateEntity("PKR", 175.74))
        dummyRateList.add(CurrencyRateEntity("USD", 1.0))
        dummyRateList.add(CurrencyRateEntity("SAR", 3.76))
        dummyWorkerLiveData.postValue(listOf(enqueuedWorkInfo))

    }

    override fun getSyncDataWorkerInfo(): LiveData<List<WorkInfo>> {
        return dummyWorkerLiveData
    }
}