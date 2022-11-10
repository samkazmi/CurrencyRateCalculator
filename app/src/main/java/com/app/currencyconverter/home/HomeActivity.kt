package com.app.currencyconverter.home

import android.os.Bundle
import com.app.currencyconverter.R
import com.app.currencyconverter.home.callback.HomeCallback
import com.app.currencyconverter.common.ui.BaseActivity
import com.app.currencyconverter.home.ui.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity(), HomeCallback {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)
        if (savedInstanceState == null) {
            addFragment(R.id.container, HomeFragment.newInstance())
        }
    }

    override fun onBackPressed(tag: String?) {
        onBackPressed()
    }
}