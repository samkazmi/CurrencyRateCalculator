package com.app.digitifysample.home.vm

import android.app.Application
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.app.digitifysample.common.extensions.isDouble
import com.app.digitifysample.common.vm.BaseViewModel
import com.app.digitifysample.datasource.models.ConversionRates
import com.app.digitifysample.datasource.usecase.CurrencyConversionUsecase
import com.mlykotom.valifi.fields.number.ValiFieldDouble
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val usecase: CurrencyConversionUsecase,
    application: Application
) : BaseViewModel(application) {
    val amount = ValiFieldDouble()
    val selectedCurrency = ObservableField("")
    lateinit var currencyList: LiveData<List<String>>
    private val currencyCodes = mutableListOf<String>()


    val conversions = MutableLiveData<PagingData<ConversionRates>>()

    init {
        getCurrencyList()
        amount.addCustomAsyncValidator("Invalid Amount") {
            it.isDouble()
        }
    }


    suspend fun shouldLoadCurrencyList() = usecase.getCurrencyListCount() <= 0

    fun loadCurrencyList() = usecase.loadAndSaveCurrencyList()

    fun getCurrencyList() {
        currencyList = Transformations.map(usecase.getCurrencyList()) {
            it.map {
                currencyCodes.add(it.currencyCode)
                it.currencyCode + " (${it.displayName})"
            }
        }
    }

    //Load currencies from within device not all currencies are supported with currencylayer
    //so only supported currencies are shown
    /*fun loadCurrencies() {
        viewModelScope.launch {
            (currencyList as MutableLiveData).value = withContext(Dispatchers.IO) {
                val toret: MutableSet<Currency> = HashSet()
                val locs: Array<Locale> = Locale.getAvailableLocales()
                for (loc in locs) {
                    try {
                        val currency: Currency = Currency.getInstance(loc)
                        toret.add(currency)
                    } catch (exc: Exception) {
                        // Locale not found
                    }
                }
                val temp = mutableListOf<String>()
                val s = toret.map {
                    temp.add(it.currencyCode)
                    it.currencyCode + " (${it.displayName})"
                }
                currencyCodes.addAll(temp.sorted())
                s.sorted()
            }
        }

    }*/

    fun getCode(position: Int): String {
        return currencyCodes[position]
    }

    fun updateConversionList() {
        viewModelScope.launch {
            val rate = usecase.findCurrencyRate(selectedCurrency.get() ?: "")
            val amount = if (amount.isValid) (amount.number ?: 0.0) else 0.0
            if (rate != null && amount > 0.0)
                usecase.getRates(amount, rate).collectLatest {
                    conversions.value = it
                }
        }
    }


    fun initOrUpdateSyncWorker() = usecase.startSyncConversionRates()

    fun observeSyncWorker() = usecase.getSyncDataWorkerInfo()

    override fun onCleared() {
        usecase.onDestroy()
        super.onCleared()

    }
}