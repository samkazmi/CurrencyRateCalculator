package com.app.currencyconverter.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.mlykotom.valifi.ValiFi
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class CurrencyApplication : Application(), Configuration.Provider {

    companion object {
        private var application: CurrencyApplication? = null
        fun getInstance(): CurrencyApplication? {
            return application
        }
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        ValiFi.install(applicationContext)
        application = this

    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

}