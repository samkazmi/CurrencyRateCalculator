package com.app.currencyconverter.datasource.repository

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.hilt.work.HiltWorkerFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.*
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.app.currencyconverter.datasource.remote.common.ApiStatus
import com.app.currencyconverter.datasource.worker.SyncDataWorker
import com.app.currencyconverter.getOrAwaitValue
import com.app.currencyconverter.datasource.repository.CurrencyRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidTest
class CurrencyRepositoryImpTest {

    @get:Rule
    var hiltrule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

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
        assert(s.rates.isNotEmpty())
        repository.saveCurrencyRates(s)
        assert(repository.getRatesCount() > 0)
    }

    @Test
    fun testCurrencyApi() {
        val value = repository.loadAndSaveCurrencyList().getOrAwaitValue()
        when (value.callInfo.status) {
            ApiStatus.LOADING -> assert(value.callInfo.isLoading)
            ApiStatus.SUCCESS -> assert(value.data?.code == 200)
            ApiStatus.ERROR -> assert(value.callInfo.error != null)
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

        MatcherAssert.assertThat(workInfo.state, Is.`is`(WorkInfo.State.RUNNING))
    }

    @Test
    //test worker
    fun testSyncWorker() {
        val worker = TestListenableWorkerBuilder<SyncDataWorker>(context)
            .setWorkerFactory(workerFactory).build()
        runBlocking {
            // this delay is because we can't call 2 endpoints simultaneously CurrencyLayer api has rate limit restrictions.
            delay(2000L)
            val result = worker.doWork()
            MatcherAssert.assertThat(result, Is.`is`(ListenableWorker.Result.success()))
        }
    }
}