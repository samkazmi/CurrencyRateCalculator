package com.app.digitifysample.datasource.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.app.digitifysample.datasource.remote.ParseErrors
import com.app.digitifysample.datasource.repository.CurrencyRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

@HiltWorker
class SyncDataWorker
@AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: CurrencyRepository,
    private val parseErrors: ParseErrors
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        try {
            repository.saveCurrencyRates(repository.getCurrencyRatesFromServer())
            return Result.success(Data.Builder().build())
        } catch (e: Exception) {
            e.printStackTrace()
            return if (e is UnknownHostException
                || e is ConnectException
                || e is SocketTimeoutException
            ) {
                Result.retry()
            } else {
                val error = parseErrors.parseError(e)
                Result.failure(
                    Data.Builder().putInt("code", error.code).putString("message", error.message)
                        .putAll(inputData).build()
                )
            }
        }
    }

    companion object {
        private val TAG = SyncDataWorker::class.java.simpleName
    }
}
