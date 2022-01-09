package com.app.digitifysample.datasource.remote.apis


import com.app.digitifysample.datasource.models.CurrencyRates
import com.app.digitifysample.datasource.models.SupportedCurrencies
import retrofit2.http.GET

interface CurrencyApi {

    @GET("live")
    suspend fun liveCurrencyRates(): CurrencyRates

    @GET("list")
    suspend fun supportedCurrencies(): SupportedCurrencies
}