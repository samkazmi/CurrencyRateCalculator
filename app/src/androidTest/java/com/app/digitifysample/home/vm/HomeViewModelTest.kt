package com.app.digitifysample.home.vm

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.WorkInfo
import com.app.digitifysample.FakeCurrencyConversionUsecase
import com.app.digitifysample.datasource.remote.common.ApiStatus
import com.app.digitifysample.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import com.mlykotom.valifi.ValiFi
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@HiltAndroidTest
class HomeViewModelTest {

    @get:Rule
    var hiltrule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    lateinit var vm: HomeViewModel

    @Before
    fun setUp() {
        hiltrule.inject()
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        ValiFi.install(context.applicationContext)
        vm = HomeViewModel(
            FakeCurrencyConversionUsecase(),
            context.applicationContext as Application
        )
    }


    @Test
    fun testInValidAmount() {
        runBlocking {
            vm.amount.set("101..")
            vm.amount.validate()
            delay(500)
            assertThat(vm.amount.error).isNotNull()
            assertThat(vm.amount.isValid).isFalse()
        }
    }

    @Test
    fun testValidAmount() {
       runBlocking {
           vm.amount.set("101")
           vm.amount.validate()
           delay(300)
           assertThat(vm.amount.isValid).isTrue()
       }
    }

    @Test
    fun testLoadCurrencies() {
        runBlocking {
            if (vm.shouldLoadCurrencyList()) {
                val it = vm.loadCurrencyList().getOrAwaitValue()
                assertThat(it.callInfo.status).isEqualTo(ApiStatus.SUCCESS)
                assertThat(vm.shouldLoadCurrencyList()).isFalse()
                val list = vm.currencyList.getOrAwaitValue()
                assertThat(list).isNotEmpty()
            }
        }
    }


    @Test
    fun testRate() {
        runBlocking {
            val ob = vm.observeSyncWorker().getOrAwaitValue()
            assertThat(ob.isEmpty())
            vm.initOrUpdateSyncWorker()
            val ob1 = vm.observeSyncWorker().getOrAwaitValue()
            assertThat(ob1.last().state).isEqualTo(WorkInfo.State.ENQUEUED)

            vm.amount.set("3.4")
            vm.selectedCurrency.set("AED")
            vm.updateConversionList()
        }
    }


}