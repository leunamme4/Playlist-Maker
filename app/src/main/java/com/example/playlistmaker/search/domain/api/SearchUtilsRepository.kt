package com.example.playlistmaker.search.domain.api

interface SearchUtilsRepository {

    fun clickDebounce(): Boolean

    fun getSearchRunnable(callback: () -> Unit): Runnable

    fun getEditTextValue() : String

    fun getLastSearch() : String

    fun setEditTextValue(value: String)

    fun setLastSearch(value: String)

    fun isClickAllowed() : Boolean
}