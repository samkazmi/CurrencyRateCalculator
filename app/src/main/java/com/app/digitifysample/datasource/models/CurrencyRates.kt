package com.app.digitifysample.datasource.models

data class CurrencyRates (
    val success: Boolean,
    val terms: String,
    val privacy: String,
    val timestamp: Long,
    val source: String,
    val quotes: Map<String, Double>
)