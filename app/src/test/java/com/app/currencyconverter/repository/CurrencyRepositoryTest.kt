package com.app.currencyconverter.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.app.currencyconverter.datasource.local.CurrencyConverterDB
import com.app.currencyconverter.datasource.local.dao.CurrencyRateDao
import com.app.currencyconverter.datasource.local.entity.CurrencyRateEntity
import com.app.currencyconverter.datasource.models.ConversionRates
import com.app.currencyconverter.datasource.models.CurrencyRates
import com.app.currencyconverter.datasource.remote.apis.CurrencyApi
import com.app.currencyconverter.datasource.repository.CurrencyRepository
import com.app.currencyconverter.datasource.repository.CurrencyRepositoryImp
import com.app.currencyconverter.datasource.utils.ParseErrors
import com.app.currencyconverter.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.json.JSONObject
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.HttpURLConnection


@RunWith(RobolectricTestRunner::class)
@Config(sdk = [29])
class CurrencyRepositoryTest {

    @get:Rule
    val testInstantTaskExecutorRule: TestRule = InstantTaskExecutorRule()

    @get: Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    private lateinit var mockWebServer: MockWebServer
    private lateinit var repository: CurrencyRepository
    private lateinit var dao: CurrencyRateDao
    private lateinit var api: CurrencyApi
    private lateinit var db: CurrencyConverterDB
    private lateinit var gson: Gson

    private val EXPCETED_ROW_COUNT = 3
    private val customCurrencyListResponse: String =
        "{\"AED\":\"United Arab Emirates Dirham\",\"AFN\":\"Afghan Afghani\",\"USD\":\"United States Dollar\"}"
    private val customRateListResponse: String =
        "{\"disclaimer\": \"Usage subject to terms: https://openexchangerates.org/terms\",\n" +
                "\"license\": \"https://openexchangerates.org/license\",\n" +
                "\"timestamp\": 1668157200,\n" +
                "\"base\": \"USD\",\n" +
                "\"rates\": {\n" +
                "\"AED\": 3.67302,\n" +
                "\"AFN\": 88.095127,\n" +
                "\"USD\": 1 }\n " +
                "}"

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, CurrencyConverterDB::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.currencyDao()

        gson = GsonBuilder()
            .setLenient()
            .setPrettyPrinting()
            .create()
        mockWebServer = MockWebServer()
        mockWebServer.start()
        api = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(OkHttpClient())
            .build()
            .create(CurrencyApi::class.java)
        repository = CurrencyRepositoryImp(api, dao, ParseErrors())

    }

    @Test
    fun `fetch currency list and check response Code 200 returned`() {
        runBlocking {
            val response = sendCustomCurrencyListResponse()
            val mockResponse = response.getBody()?.readUtf8()
            val actualResponse = repository.loadCurrencyList()
            assertThat(mockResponse).isEqualTo(JSONObject(actualResponse).toString())
        }
    }

    @Test
    fun `fetch currency list and save in db and check if it is saved`() {
        runBlocking {
            val response = sendCustomCurrencyListResponse()
            val mockResponse = gson.fromJson(response.getBody()?.readUtf8(), Map::class.java)
            val actualResponse = repository.loadCurrencyList()
            assertThat(mockResponse).isEqualTo(actualResponse)
            repository.saveCurrencyList(actualResponse)
            val count = repository.getCurrencyListCount()
            assertThat(count).isEqualTo(EXPCETED_ROW_COUNT)
        }
    }

    @Test
    fun `fetch rate list and check response Code 200 returned`() {
        runBlocking {
            val response = sendCustomRateListResponse()
            val mockResponse =
                gson.fromJson(response.getBody()?.readUtf8(), CurrencyRates::class.java)
            val actualResponse = repository.getCurrencyRatesFromServer()
            assertThat(mockResponse).isEqualTo(actualResponse)
        }
    }


    @Test
    fun `fetch rate list and save in db and check if it is saved`() {
        runBlocking {
            loadAndSaveCurrencyList()
            sendCustomRateListResponse()
            repository.saveCurrencyRates(repository.getCurrencyRatesFromServer())
            val count = repository.getRatesCount()
            assertThat(count).isEqualTo(EXPCETED_ROW_COUNT)
        }
    }

    @Test
    fun `fetch currency list from server and save and get from db`() {
        runBlocking {
            loadAndSaveCurrencyList()
            val r = repository.getCurrencyList().getOrAwaitValue()
            assertThat(r.size).isEqualTo(EXPCETED_ROW_COUNT)
        }
    }

    @Test
    fun `fetch rate list from server and save and get from db`() {
        runBlocking {
            loadAndSaveCurrencyList()
            sendCustomRateListResponse()
            repository.saveCurrencyRates(repository.getCurrencyRatesFromServer())
            val s = dao.getAllRatesList()
            assertThat(s.size).isEqualTo(EXPCETED_ROW_COUNT)
        }
    }

    @Test
    fun `find currency rate from db see if it matches`() {
        runBlocking {
            loadAndSaveCurrencyList()
            sendCustomRateListResponse()
            repository.saveCurrencyRates(repository.getCurrencyRatesFromServer())
            val s = repository.findCurrencyRate("AED")
            assertThat(s).isEqualTo(CurrencyRateEntity("AED", 3.67302))
        }
    }

    @Test
    fun `find currency rate from db and calculate the amount and match the conversion rate`() {
        runBlocking {
            loadAndSaveCurrencyList()
            sendCustomRateListResponse()
            repository.saveCurrencyRates(repository.getCurrencyRatesFromServer())
            val usd = repository.findCurrencyRate("USD")
            val aed = repository.findCurrencyRate("AED")
            val conversionRate = usd?.toConversionRate(5.0, aed!!)
            assertThat(conversionRate).isEqualTo(ConversionRates("AED", "USD", 1.3612776407424951))
        }
    }

    private suspend fun loadAndSaveCurrencyList() {
        sendCustomCurrencyListResponse()
        repository.saveCurrencyList(repository.loadCurrencyList())
    }


    private fun sendCustomCurrencyListResponse(): MockResponse {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(customCurrencyListResponse)
            .setResponseCode(200)
        mockWebServer.enqueue(response)
        return response
    }

    private fun sendCustomRateListResponse(): MockResponse {
        val response = MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setBody(customRateListResponse)
            .setResponseCode(200)
        mockWebServer.enqueue(response)
        return response
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
        mockWebServer.shutdown()
        Mockito.validateMockitoUsage()
    }

}