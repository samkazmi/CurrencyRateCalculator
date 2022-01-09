package com.app.digitifysample.common.callbacks

interface NetworkStateDialogCallback {
    fun onErrorDialogRetryButtonClicked(endpointTag: String)
    fun onErrorDialogClosed(endpointTag: String)
}
