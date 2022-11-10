package com.app.currencyconverter.datasource.remote.common

import com.google.gson.annotations.SerializedName


/**
 *
 * @param error
 */
data class ErrorModel(
    val code: Int = 0,
    @SerializedName("message")
    val error: String?,
    val message: List<String>?
)