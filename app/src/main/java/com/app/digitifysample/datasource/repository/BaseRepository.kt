package com.app.digitifysample.datasource.repository

import com.app.digitifysample.datasource.remote.ParseErrors

open class BaseRepository(private val parseErrors: ParseErrors) {

    suspend fun handleException(e: Exception) = parseErrors.parseError(e)
}