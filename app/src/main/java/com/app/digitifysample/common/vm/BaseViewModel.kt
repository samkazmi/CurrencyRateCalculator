package com.app.digitifysample.common.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {

    fun cancelRequest() {
        viewModelScope.cancel()
    }


}