package com.app.currencyconverter.home.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.work.WorkInfo
import com.app.currencyconverter.common.adapter.PagingLoadStateAdapter
import com.app.currencyconverter.common.extensions.*
import com.app.currencyconverter.common.ui.BaseFragment
import com.app.currencyconverter.databinding.HomeFragmentBinding
import com.app.currencyconverter.datasource.models.ConversionRates
import com.app.currencyconverter.datasource.remote.common.ApiStatus
import com.app.currencyconverter.home.adapter.ConversionRateListAdapter
import com.app.currencyconverter.home.callback.HomeCallback
import com.app.currencyconverter.home.callback.HomeViewCallback
import com.app.currencyconverter.home.vm.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BaseFragment(), HomeViewCallback {

    companion object {
        val TAG = HomeFragment::class.java.simpleName
        private const val RETRY_CURRENCY_TAG = "getCurrencies"
        private const val RETRY_RATE_TAG = "getRates"
        fun newInstance() = HomeFragment()
    }

    private val vm: HomeViewModel by viewModels()
    private lateinit var binding: HomeFragmentBinding
    private val adapter = ConversionRateListAdapter(this)
    private var callback: HomeCallback? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomeCallback) {
            callback = context
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadCurrencies()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.callback = this
        binding.vm = vm
        return binding.root
    }

    @OptIn(FlowPreview::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCurrencyList()
        initList()
        amountCheck()
        observeSyncWork()
    }

    @FlowPreview
    @ExperimentalCoroutinesApi
    private fun amountCheck() {
        binding.tilAmount.editText?.textChanges()?.debounce(300)?.filter {
            it.toString().isDouble()
        }?.onEach {
            loadRates()
        }?.launchIn(lifecycleScope)
    }

    private fun initCurrencyList() {
        binding.sCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                vm.selectedCurrency.set(vm.getCode(position))
                vm.updateConversionList()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
        vm.currencyList.observe(viewLifecycleOwner) {
            binding.sCurrency.adapter = ArrayAdapter(
                requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1,
                it
            )
        }
    }

    private fun loadCurrencies() {
        lifecycleScope.launch {
            if (vm.shouldLoadCurrencyList()) {
                vm.loadCurrencyList().observe(viewLifecycleOwner) {
                    when (it.callInfo.status) {
                        ApiStatus.LOADING -> {
                            showProgress()
                        }
                        ApiStatus.SUCCESS -> {
                            hideProgress()
                        }
                        ApiStatus.ERROR -> {
                            onErrorDialog(it.callInfo.error, RETRY_CURRENCY_TAG)
                        }
                    }
                }
            }
        }
    }

    private fun initList() {
        //binding.rvSelectableList.layoutManager = LinearLayoutManager(context)
        adapter.addLoadStateListener { loadState ->
            if (loadState.refresh is LoadState.Loading) {
                binding.srlRefresh.isRefreshing = true
            } else {
                binding.srlRefresh.isRefreshing = false

                // getting the error
                val error = when {
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                error?.let {
                    onErrorSimple(message = it.error.toString())
                }
            }
        }
        /*lifecycleScope.launch {
            adapter.loadStateFlow
                .distinctUntilChanged { old, new ->
                    old.mediator?.prepend?.endOfPaginationReached.isTrue() ==
                            new.mediator?.prepend?.endOfPaginationReached.isTrue()
                }
                .filter { it.refresh is LoadState.NotLoading && it.prepend.endOfPaginationReached && !it.append.endOfPaginationReached }
                .collect {
                    binding.rvSelectableList.scrollToPosition(0)
                }
        }*/
        vm.conversions.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }
        binding.rvSelectableList.adapter = adapter.withLoadStateFooter(
            footer = PagingLoadStateAdapter { adapter.retry() }
        )
        binding.srlRefresh.setOnRefreshListener {
            vm.updateConversionList()
            adapter.refresh()
        }

    }

    private fun loadRates() {
        vm.initOrUpdateSyncWorker()
        vm.updateConversionList()
    }

    private fun observeSyncWork() {
        vm.observeSyncWorker().observe(viewLifecycleOwner) {
            if (it.isNotEmpty())
                when (it.last().state) {
                    WorkInfo.State.ENQUEUED -> {
                        if (isShowingProgress()) {
                            /* if (vm.shouldLoadRates()) {
                                 onErrorSimple(901, "Unable to load rates", RETRY_RATE_TAG)
                             } else {*/
                            vm.updateConversionList()
                            hideProgress()
                            //}
                        }
                    }
                    WorkInfo.State.RUNNING -> {
                        showProgress()
                    }
                    WorkInfo.State.FAILED -> {
                        hideProgress()
                        val code = it.last().outputData.getInt("code", 901)
                        val message =
                            it.last().outputData.getString("message") ?: "Unable to fetch rate"
                        onErrorSimple(code, message, RETRY_RATE_TAG)
                    }
                    else -> {
                        hideProgress()
                    }
                }

        }
    }

    override fun onListItemClicked(item: ConversionRates, position: Int) {
        /*TODO callback for conversionRates*/
    }

    override fun onBackPressed() {
        // if we have custom back button on toolbar
        callback?.onBackPressed(tag)
    }

    override fun onErrorDialogRetryButtonClicked(endpointTag: String) {
        super.onErrorDialogRetryButtonClicked(endpointTag)
        if (endpointTag == RETRY_CURRENCY_TAG) {
            loadCurrencies()
        } else if (endpointTag == RETRY_RATE_TAG) {
            loadRates()
        }
    }

    override fun onErrorDialogClosed(endpointTag: String) {
        super.onErrorDialogClosed(endpointTag)
        if (endpointTag == RETRY_CURRENCY_TAG || endpointTag == RETRY_RATE_TAG) {
            toast("App is closing..")
            onBackPressed()
        }
    }

}