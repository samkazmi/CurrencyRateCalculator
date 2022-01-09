package com.app.digitifysample.common.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.app.digitifysample.common.callbacks.ProgressDialogCallback


abstract class BaseActivity : AppCompatActivity(), ProgressDialogCallback {


    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
    }
    /*To be called in onAttachedToWindow*/
    protected fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
        }
    }


    protected fun showProgress(title: String = "", message: String = "") {
        ProgressDialogFragment.newInstance(title, message)
            .show(supportFragmentManager, "showProgress")
    }

    protected fun hideProgress() {
        supportFragmentManager.findFragmentByTag("showProgress")?.let {
            (it as DialogFragment).dismiss()
        }
    }

    fun onLoading(): (Boolean) -> Unit {
        return {
            if (it) {
                showProgress()
            } else {
                hideProgress()
            }
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


    protected fun findFragment(id: Int) = supportFragmentManager.findFragmentById(id)
    protected fun findFragment(tag: String?) = supportFragmentManager.findFragmentByTag(tag)
    protected fun popNow() {
        try {
            supportFragmentManager.popBackStackImmediate()
        } catch (e: Exception) {
            Log.v("FragmentManager", e.toString())
            /*try {
                supportFragmentManager.executePendingTransactions()
            } catch (e: Exception) {
                Log.v("FragmentManager",e.toString())
            }*/
        }
    }

    fun toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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