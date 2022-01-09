package com.app.digitifysample.common.callbacks

interface RecyclerViewCallback<T> {
    fun onListItemClicked(item : T,position: Int)
}