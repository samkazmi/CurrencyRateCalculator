package com.app.currencyconverter.home.callback

import com.app.currencyconverter.common.callbacks.RecyclerViewCallback
import com.app.currencyconverter.datasource.models.ConversionRates

interface HomeViewCallback : RecyclerViewCallback<ConversionRates> {
    fun onBackPressed()
}