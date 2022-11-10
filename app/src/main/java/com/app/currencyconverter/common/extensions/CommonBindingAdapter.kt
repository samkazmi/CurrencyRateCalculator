package com.app.currencyconverter.common.extensions

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout


@BindingAdapter(value = ["errorMessage"], requireAll = true)
fun setError(et: TextInputLayout, errorMessage: String?) {
    et.error = errorMessage
}
