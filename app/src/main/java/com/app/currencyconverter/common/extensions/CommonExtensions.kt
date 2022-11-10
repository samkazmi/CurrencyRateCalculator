package com.app.currencyconverter.common.extensions

import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.app.currencyconverter.common.ui.ProgressDialogFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart


fun Boolean?.isTrue() = this != null && this

fun String?.isDouble() = if (this.isNullOrEmpty()) false else try {
    this.toDouble()
    true
} catch (e: NumberFormatException) {
    false
}

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        // checkMainThread()
        val listener = doOnTextChanged { text, _, _, _ -> trySend(text) }
        awaitClose { removeTextChangedListener(listener) }
    }.onStart { emit(text) }
}

fun Fragment.showProgress(title: String = "", message: String = "") {
    if (!isAdded) return
    val fragment = childFragmentManager.findFragmentByTag("showProgress")
    if (fragment == null)
        ProgressDialogFragment.newInstance(title, message)
            .show(childFragmentManager, "showProgress")
}

fun Fragment.isShowingProgress() = childFragmentManager.findFragmentByTag("showProgress") != null

fun Fragment.hideProgress() {
    try {
        if (isAdded) {
            childFragmentManager.fragments.filter {
                it.tag == "showProgress"
            }.forEach {
                (it as DialogFragment).dismissAllowingStateLoss()
            }
        }
    } catch (e: Exception) {
    }
}
