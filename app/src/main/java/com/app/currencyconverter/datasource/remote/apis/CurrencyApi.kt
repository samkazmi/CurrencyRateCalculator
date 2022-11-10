package com.app.currencyconverter.datasource.remote.apis


import com.app.currencyconverter.datasource.models.CurrencyRates
import com.app.currencyconverter.datasource.models.SupportedCurrencies
import retrofit2.http.GET

interface CurrencyApi {
    @GET("latest.json")
    suspend fun liveCurrencyRates(): CurrencyRates

    @GET("currencies.json")
    suspend fun supportedCurrencies(): Map<String, String>
}