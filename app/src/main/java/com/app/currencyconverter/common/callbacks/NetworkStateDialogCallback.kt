package com.app.currencyconverter.common.callbacks

interface NetworkStateDialogCallback {
    fun onErrorDialogRetryButtonClicked(endpointTag: String)
    fun onErrorDialogClosed(endpointTag: String)
}
