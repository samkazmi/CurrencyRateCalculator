package com.app.currencyconverter.datasource.local.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.app.currencyconverter.datasource.models.ConversionRates

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = CurrencyEntity::class,
            parentColumns = ["currencyCode"],
            childColumns = ["code"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class CurrencyRateEntity(
    @PrimaryKey val code: String,
    val rate: Double
) {
    fun toConversionRate(
        selectedAmount: Double,
        selectedCurrency: CurrencyRateEntity
    ): ConversionRates {
        val amount = (rate / selectedCurrency.rate) * selectedAmount
        return ConversionRates(selectedCurrency.code, code, amount)
    }
}