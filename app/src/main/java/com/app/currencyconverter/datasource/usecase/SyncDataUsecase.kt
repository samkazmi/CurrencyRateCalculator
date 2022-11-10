package com.app.currencyconverter.datasource.usecase

interface SyncDataUsecase {

    fun checkIfCommonDataIsPresent(onResult: (Boolean) -> Unit)
    fun onDestroy()
    fun observeWorker()
    fun syncCommonData()
    fun removeObserver()
    fun syncDataPeriodically() {}
}