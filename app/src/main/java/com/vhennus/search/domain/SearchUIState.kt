package com.vhennus.search.domain

data class SearchUIState(
    val isSearchLoading:Boolean = false,
    val isSearchSuccess:Boolean = false,
    val isSearchError:Boolean = false,
    val searchErrorMessage:String = ""
)
