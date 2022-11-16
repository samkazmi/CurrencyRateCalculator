package com.app.currencyconverter.common.ui

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.app.currencyconverter.common.callbacks.ProgressDialogCallback


abstract class BaseActivity : AppCompatActivity(), ProgressDialogCallback {


    protected fun showProgress(title: String = "", message: String = "") {
        ProgressDialogFragment.newInstance(title, message)
            .show(supportFragmentManager, "showProgress")
    }

    protected fun hideProgress() {
        supportFragmentManager.findFragmentByTag("showProgress")?.let {
            (it as DialogFragment).dismiss()
        }
    }

    override fun onProgressDialogCancelled() {

    }

    protected fun addFragment(
        @IdRes resId: Int, fragment: Fragment,
        addToBackStack: Boolean = false
    ) {
        commitFragment(
            resId,
            fragment,
            addToBackStack,
            supportFragmentManager,
            false
        )
    }

    protected fun replaceFragment(
        @IdRes resId: Int, fragment: Fragment,
        addToBackStack: Boolean = false
    ) {
        commitFragment(resId, fragment, addToBackStack, supportFragmentManager, true)
    }

    protected fun commitFragment(
        @IdRes resId: Int, fragment: Fragment,
        addToBackStack: Boolean,
        fragmentManager: FragmentManager,
        replaceFragment: Boolean
    ) {
        try {
            commit(
                getFragmentTransaction(
                    resId,
                    fragment,
                    addToBackStack,
                    fragmentManager,
                    replaceFragment
                )
            )
        } catch (e: IllegalStateException) {
            Log.e("IllegalStateException", "Commit fragment: $e")
            e.message?.contains("onSaveInstanceState")?.let {
                commitAllowStateLoss(
                    getFragmentTransaction(
                        resId,
                        fragment,
                        addToBackStack,
                        fragmentManager,
                        replaceFragment
                    )
                )
            }
        }

    }

    private fun getFragmentTransaction(
        @IdRes resId: Int, fragment: Fragment,
        addToBackStack: Boolean,
        fragmentManager: FragmentManager,
        replaceFragment: Boolean
    ): FragmentTransaction {
        val fragmentTransaction =
            createFragmentTransaction(
                resId,
                fragment,
                fragmentManager,
                replaceFragment
            )
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
        }
        return fragmentTransaction
    }

    protected fun showDialog(fragment: DialogFragment) {
        try {
            commit(createDialogFragmentTransaction(fragment))
        } catch (e: IllegalStateException) {
            Log.e("IllegalStateException", "Commit fragment: $e")
            e.message?.contains("onSaveInstanceState")?.let {
                commitAllowStateLoss(createDialogFragmentTransaction(fragment))
            }
        }

    }

    private fun createDialogFragmentTransaction(fragment: DialogFragment): FragmentTransaction {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(fragment, fragment.javaClass.simpleName)
        return transaction
    }

    @Throws(IllegalStateException::class)
    protected fun commit(fragmentTransaction: FragmentTransaction) {
        fragmentTransaction.commit()
    }

    @Throws(IllegalStateException::class)
    protected fun commitAllowStateLoss(fragmentTransaction: FragmentTransaction) {
        fragmentTransaction.commit()
    }


    protected fun createFragmentTransaction(
        @IdRes resId: Int, fragment: Fragment,
        fragmentManager: FragmentManager,
        replaceFragment: Boolean
    ): FragmentTransaction {
        val fragmentTransaction = fragmentManager.beginTransaction()
        if (replaceFragment) {
            fragmentTransaction.replace(resId, fragment, fragment.javaClass.simpleName)
        } else {
            fragmentTransaction.add(resId, fragment, fragment.javaClass.simpleName)
        }
        return fragmentTransaction
    }

    protected fun closeKeyboard(): Boolean {
        var isKeyboardOpen = false
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            isKeyboardOpen = imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        return isKeyboardOpen
    }

    override fun onDestroy() {
        hideProgress()
        super.onDestroy()
    }
}