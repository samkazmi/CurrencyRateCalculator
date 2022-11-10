package com.app.currencyconverter.datasource.local.entity

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CurrencyRateEntityTest {

    @Test
    fun toConversionRate() {
        val source = CurrencyRateEntity("AED", 3.67)
        val conversionRate = source.toConversionRate(100.0, CurrencyRateEntity("PKR", 75.0))
        assertThat(conversionRate.amount).isWithin(0.1).of(4.89)
    }
}