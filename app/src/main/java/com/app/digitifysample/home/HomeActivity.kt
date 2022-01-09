package com.app.digitifysample.home

import android.os.Bundle
import com.app.digitifysample.R
import com.app.digitifysample.home.callback.HomeCallback
import com.app.digitifysample.common.ui.BaseActivity
import com.app.digitifysample.home.ui.HomeFragment
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