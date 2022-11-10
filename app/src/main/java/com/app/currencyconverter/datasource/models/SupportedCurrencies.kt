package com.app.currencyconverter.datasource.models

data class SupportedCurrencies(
    val success: Boolean,
    val terms: String,
    val privacy: String,
    val currencies: Map<String, String>
)
