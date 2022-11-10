package com.app.currencyconverter.common.callbacks

interface RecyclerViewCallback<T> {
    fun onListItemClicked(item : T,position: Int)
}