package com.app.currencyconverter.common.callbacks

interface PagedListViewCallback<Item> : PagedAdapterCallback<Item>, NetworkStateListCallback {
    override fun onPageReload()

    override fun onListItemClicked(item: Item, position: Int)

    override fun onRetryButtonClicked()
}