package com.app.digitifysample.datasource.models

data class CurrencyRates (
    val disclaimer: String,
    val license: String,
    val timestamp: Long,
    val base: String,
    val rates: Map<String, Double>
)
