package com.app.currencyconverter

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.mlykotom.valifi.ValiFi
import dagger.hilt.android.testing.CustomTestApplication
import dagger.hilt.android.testing.HiltTestApplication


class CustomTestRunner : AndroidJUnitRunner() {
    override fun newApplication(
        cl: ClassLoader?,
        className: String?,
        context: Context?
    ): Application {
        return super.newApplication(cl, HiltTestApplication::class.java.name, context)
    }
}

