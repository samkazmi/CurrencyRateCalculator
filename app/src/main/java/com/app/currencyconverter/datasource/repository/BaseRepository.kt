package com.app.currencyconverter.datasource.repository

import com.app.currencyconverter.datasource.utils.ParseErrors

open class BaseRepository(private val parseErrors: ParseErrors) {

    suspend fun handleException(e: Exception) = parseErrors.parseError(e)
}