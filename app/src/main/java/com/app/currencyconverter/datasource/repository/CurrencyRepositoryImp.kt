package com.app.currencyconverter.datasource.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.app.currencyconverter.datasource.local.dao.CurrencyRateDao
import com.app.currencyconverter.datasource.local.entity.CurrencyEntity
import com.app.currencyconverter.datasource.local.entity.CurrencyRateEntity
import com.app.currencyconverter.datasource.models.CurrencyRates
import com.app.currencyconverter.datasource.models.SupportedCurrencies
import com.app.currencyconverter.datasource.remote.ParseErrors
import com.app.currencyconverter.datasource.remote.apis.CurrencyApi
import com.app.currencyconverter.datasource.remote.common.LiveResponse
import com.app.currencyconverter.datasource.remote.common.Message


class CurrencyRepositoryImp constructor(
    private val api: CurrencyApi,
    private val dao: CurrencyRateDao,
    parseErrors: ParseErrors
) : BaseRepository(parseErrors), CurrencyRepository {

    override suspend fun getCurrencyRatesFromServer() = api.liveCurrencyRates()


    override suspend fun saveCurrencyRates(c: CurrencyRates) {
        dao.insertAllRates(c.rates.map {
            CurrencyRateEntity(
                it.key,
                it.value
            )
        })
    }

    override fun getCurrencyRatesFromDbPaged() = dao.getAllRatesPaged()

    override fun loadAndSaveCurrencyList(): LiveData<LiveResponse<Message>> {
        return liveData {
            emit(LiveResponse.loading())
            try {
                val res = api.supportedCurrencies()
                saveCurrencyList(res)
                emit(LiveResponse.success(Message(200, "success")))
            } catch (e: Exception) {
                emit(LiveResponse.error(handleException(e)))
            }
        }
    }

    override suspend fun findCurrencyRate(currencyCode: String) = dao.getCurrencyRate(currencyCode)
    override suspend fun getRatesCount() = dao.getRatesCount()

    override fun getCurrencyList() = dao.getCurrencyListLive()

    suspend fun saveCurrencyList(currencyMap: Map<String, String>) {
        dao.insertAllCurrencies(currencyMap.toList().map {
            CurrencyEntity(
                it.first,
                it.second
            )
        })
    }

    override suspend fun getCurrencyListCount(): Int {
       return dao.getCurrencyCount()
    }


}