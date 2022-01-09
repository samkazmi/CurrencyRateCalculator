package com.app.digitifysample.home

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.app.digitifysample.datasource.remote.common.ApiStatus
import com.app.digitifysample.datasource.repository.CurrencyRepository
import com.app.digitifysample.datasource.worker.SyncDataWorker
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidTest
class RepoTest {

    @get:Rule
    var hiltrule = HiltAndroidRule(this)

    var handler: Handler = Handler(Looper.getMainLooper())

    @Inject
    lateinit var repository: CurrencyRepository


    lateinit var context: Context

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    lateinit var workManager: WorkManager

    @Before
    fun setUp() {
        hiltrule.inject()
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setWorkerFactory(workerFactory)
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        WorkManager.initialize(context, config)
        workManager = WorkManager.getInstance(context)
    }

    @Test
    fun testRates() = runBlocking {
        val s = repository.getCurrencyRatesFromServer()
        assert(s.success)
        repository.saveCurrencyRates(s)
        assert(repository.getRatesCount() > 0)
    }

    @Test
    fun testCurrencyApi() {
        handler.post {
            repository.loadAndSaveCurrencyList().observeForever {
                when (it.callInfo.status) {
                    ApiStatus.LOADING -> assert(it.callInfo.isLoading)
                    ApiStatus.SUCCESS -> assert(it.data?.code == 200)
                    ApiStatus.ERROR -> assert(it.callInfo.error != null)
                }
            }
        }
    }

    @Test
    fun testCurrencyCount() = runBlocking {
        assert(repository.getCurrencyListCount() > 0)
    }

    @Test
    //test constraints and check perodic functionality
    fun testWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = PeriodicWorkRequestBuilder<SyncDataWorker>(30, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)

        workManager.enqueue(request).result.get()
        testDriver?.setAllConstraintsMet(request.id)
        testDriver?.setPeriodDelayMet(request.id)
        val workInfo = workManager.getWorkInfoById(request.id).get()

        assertThat(workInfo.state, `is`(WorkInfo.State.RUNNING))
    }

    @Test
    //test worker
    fun testSleepWorker() {
        val worker =
            TestListenableWorkerBuilder<SyncDataWorker>(context).setWorkerFactory(
                workerFactory
            ).build()
        runBlocking {
            delay(2000L)
            val result = worker.doWork()
            assertThat(result, `is`(ListenableWorker.Result.success()))
        }
    }
}