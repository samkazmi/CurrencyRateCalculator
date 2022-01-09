package com.app.digitifysample.datasource.remote.common


data class PageStatus(val pageStatus: PageState, val message: Message? = null)
enum class PageState {
    LOADING,
    EMPTY,
    SUCCESS,
    ERROR
}
