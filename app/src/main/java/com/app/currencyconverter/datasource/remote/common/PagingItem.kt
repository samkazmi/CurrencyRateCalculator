package com.app.currencyconverter.datasource.remote.common

data class PagingItem<Item>(val pageInfo: PageInfo, val items: List<Item> = listOf())