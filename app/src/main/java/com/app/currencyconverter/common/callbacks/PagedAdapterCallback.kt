package com.app.currencyconverter.common.callbacks

interface PagedAdapterCallback<Item> : LoaderViewItemCallback, RecyclerViewCallback<Item> {
    override fun onPageReload()
    override fun onListItemClicked(item: Item, position: Int)
}
