package com.app.digitifysample.datasource.models

data class ConversionRates (
    val targetCurrency: String,
    val sourceCurrency: String,
    val amount: Double
)