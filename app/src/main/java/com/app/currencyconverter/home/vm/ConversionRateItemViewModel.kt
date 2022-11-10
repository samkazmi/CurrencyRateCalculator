package com.app.currencyconverter.home.vm


import android.content.Context
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.app.currencyconverter.datasource.models.ConversionRates
import java.math.RoundingMode
import java.text.DecimalFormat


class ConversionRateItemViewModel constructor(private val context: Context) : ViewModel() {
    lateinit var item: ConversionRates

    val currencyCode = ObservableField("")
    val amount = ObservableField("")
    private val format = DecimalFormat.getInstance()

    init {
        format.roundingMode = RoundingMode.UP
        format.maximumFractionDigits = 2
        format.minimumIntegerDigits = 2
    }
    fun setData(item: ConversionRates) {
        this.item = item
        currencyCode.set(item.sourceCurrency)
        amount.set(format.format(item.amount))
    }

}
