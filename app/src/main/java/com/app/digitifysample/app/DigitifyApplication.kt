package com.app.digitifysample.app

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.mlykotom.valifi.ValiFi
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DigitifyApplication : Application(), Configuration.Provider {

    companion object {
        private var application: DigitifyApplication? = null
        fun getInstance(): DigitifyApplication? {
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