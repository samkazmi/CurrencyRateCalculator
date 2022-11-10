package com.app.currencyconverter.common.ui

import android.content.res.Configuration
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.app.currencyconverter.datasource.remote.common.Message
import com.app.currencyconverter.common.callbacks.NetworkStateDialogCallback
import com.app.currencyconverter.common.callbacks.ProgressDialogCallback
import com.app.currencyconverter.common.extensions.hideProgress
import com.app.currencyconverter.common.extensions.showProgress

abstract class BaseFragment : Fragment(), ProgressDialogCallback, NetworkStateDialogCallback {

    override fun onErrorDialogRetryButtonClicked(endpointTag: String) {
        hideErrorDialog()
    }

    override fun onErrorDialogClosed(endpointTag: String) {
        hideErrorDialog()
    }

    override fun onProgressDialogCancelled() {

    }

    public fun isUsingNightModeResources(): Boolean {
        return when (resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            Configuration.UI_MODE_NIGHT_UNDEFINED -> false
            else -> false
        }
    }

    fun toast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun onErrorSimple(code: Int = 0, message: String, endpointTag: String? = null) {
        if (message.isNotEmpty() && message.isNotBlank())
            showErrorDialog(code, message, endpointTag)
    }

    private fun showErrorDialog(code: Int = 0, message: String = "", endpointTag: String?) {
        try {
            NetworkStateDialogFragment.newInstance(code, message, endpointTag)
                .show(childFragmentManager, "NetworkStateDialogFragment")
        } catch (e: Exception) {

        }

    }

    private fun hideErrorDialog() {
        try {
            childFragmentManager.findFragmentByTag("NetworkStateDialogFragment")?.let {
                (it as DialogFragment).dismissAllowingStateLoss()
            }
        } catch (e: Exception) {

        }
    }



    fun onLoading(isLoading: Boolean) {
        if (isAdded)
            if (isLoading) {
                showProgress()
            } else {
                hideProgress()
            }
    }

    fun onErrorDialog(error: Message?, tag: String? = null) {
        error?.let {
            onErrorSimple(it.code, it.message, tag)
        }
    }

    fun onErrorToast(error: Message?) {
        error?.let {
            toast(error.code.toString() + "\n" + it.message)
        }
    }


    override fun onDestroyView() {
        hideProgress()
        hideErrorDialog()
        super.onDestroyView()
    }
}