package com.app.currencyconverter.datasource.remote.common


data class PageInfo(
    var page: Int? = null,
    val totalPages: Int? = null,
    val totalItems: Int? = null,
    val itemsPerPage: Int? = null
)