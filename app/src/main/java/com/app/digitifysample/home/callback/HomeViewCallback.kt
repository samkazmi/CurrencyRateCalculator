package com.app.digitifysample.home.callback

import com.app.digitifysample.common.callbacks.RecyclerViewCallback
import com.app.digitifysample.datasource.models.ConversionRates

interface HomeViewCallback : RecyclerViewCallback<ConversionRates> {
    fun onBackPressed()
}