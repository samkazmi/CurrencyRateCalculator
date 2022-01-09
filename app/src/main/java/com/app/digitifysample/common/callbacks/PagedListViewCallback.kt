package com.app.digitifysample.common.callbacks

interface PagedListViewCallback<Item> : PagedAdapterCallback<Item>, NetworkStateListCallback {
    override fun onPageReload()

    override fun onListItemClicked(item: Item, position: Int)

    override fun onRetryButtonClicked()
}