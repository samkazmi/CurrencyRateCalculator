package com.app.digitifysample.datasource.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.liveData
import com.app.digitifysample.datasource.remote.ParseErrors
import com.app.digitifysample.datasource.remote.common.LiveResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow

open class BaseRepository(private val parseErrors: ParseErrors) {


    fun <T, M> transforms(
        apiCall: LiveData<LiveResponse<T>>,
        transforms: (LiveResponse<T>) -> LiveResponse<M>
    ) = Transformations.map(apiCall) {
        try {
            transforms(it)
        } catch (e: Exception) {
            LiveResponse.error(parseErrors.parseException(e))
        }
    }


    fun <T> callApi(apiCall: suspend () -> T) = liveData(Dispatchers.IO) {
        emit(LiveResponse.loading())
        try {
            emit(LiveResponse.success(apiCall()))
        } catch (e: Exception) {
            emit(LiveResponse.error<T>(handleException(e)))
        }
    }

    fun <T> callApiFlow(apiCall: suspend () -> T) = flow {
        emit(LiveResponse.loading())
        try {
            emit(LiveResponse.success(apiCall()))
        } catch (e: Exception) {
            emit(LiveResponse.error<T>(handleException(e)))
        }
    }


    suspend fun handleException(e: Exception) = parseErrors.parseError(e)
}