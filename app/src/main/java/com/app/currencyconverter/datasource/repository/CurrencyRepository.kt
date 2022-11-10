package com.app.currencyconverter.datasource.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import com.app.currencyconverter.datasource.remote.common.LiveResponse
import com.app.currencyconverter.datasource.remote.common.Message
import com.app.currencyconverter.datasource.local.entity.CurrencyEntity
import com.app.currencyconverter.datasource.local.entity.CurrencyRateEntity
import com.app.currencyconverter.datasource.models.CurrencyRates
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {

    suspend fun getCurrencyRatesFromServer(): CurrencyRates
    suspend fun saveCurrencyRates(c: CurrencyRates)
    //fun getCurrencyRatesFromDb(): Flow<List<CurrencyRateEntity>>
    fun getCurrencyRatesFromDbPaged(): PagingSource<Int, CurrencyRateEntity>

    fun getCurrencyList(): LiveData<List<CurrencyEntity>>
    suspend fun getCurrencyListCount(): Int
    fun loadAndSaveCurrencyList(): LiveData<LiveResponse<Message>>

    suspend fun findCurrencyRate(currencyCode: String): CurrencyRateEntity?
    suspend fun getRatesCount(): Int

}