package com.app.currencyconverter.home

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.app.currencyconverter.R
import com.app.currencyconverter.common.ui.BaseActivity
import com.app.currencyconverter.home.callback.HomeCallback
import com.app.currencyconverter.home.ui.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity(), HomeCallback {


    private val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        onBackPressedDispatcher.addCallback(this, callback)
        if (savedInstanceState == null) {
            addFragment(R.id.container, HomeFragment.newInstance())
        }
    }

    override fun onBackPressed(tag: String?) {
        callback.handleOnBackPressed()

    }
}