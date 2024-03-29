package com.app.currencyconverter.datasource.usecase

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.work.WorkInfo
import com.app.currencyconverter.datasource.remote.common.LiveResponse
import com.app.currencyconverter.datasource.remote.common.Message
import com.app.currencyconverter.datasource.local.entity.CurrencyEntity
import com.app.currencyconverter.datasource.local.entity.CurrencyRateEntity
import com.app.currencyconverter.datasource.models.ConversionRates
import kotlinx.coroutines.flow.Flow

interface CurrencyConversionUsecase {

    fun getRates(): Flow<PagingData<CurrencyRateEntity>>
    fun getRates(
        selectedAmount: Double,
        selectedCurrency: CurrencyRateEntity
    ): Flow<PagingData<ConversionRates>>
    // fun getRates(value: Double, currency: String): Flow<List<CurrencyRateEntity>>

    fun getCurrencyList(): LiveData<List<CurrencyEntity>>
    suspend fun getCurrencyListCount(): Int
    suspend fun getRatesCount(): Int
    fun loadAndSaveCurrencyList(): LiveData<LiveResponse<Message>>
    fun onDestroy()
    suspend fun findCurrencyRate(currencyCode: String): CurrencyRateEntity?

    fun updateSyncConversionRates()
    fun forceSyncConversionRates()
    fun getSyncDataWorkerInfo(): LiveData<List<WorkInfo>>
}