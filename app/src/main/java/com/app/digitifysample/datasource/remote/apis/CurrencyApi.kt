package com.app.digitifysample.datasource.remote.apis


import com.app.digitifysample.datasource.models.CurrencyRates
import com.app.digitifysample.datasource.models.SupportedCurrencies
import retrofit2.http.GET

interface CurrencyApi {
    @GET("latest.json")
    suspend fun liveCurrencyRates(): CurrencyRates

    @GET("currencies.json")
    suspend fun supportedCurrencies(): Map<String, String>
}