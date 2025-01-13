package com.example.playlistmaker.data

import com.example.playlistmaker.domain.api.SearchUtilsRepository

class SearchUtilsRepositoryImpl(private val searchUtils: SearchUtils) : SearchUtilsRepository {
    override fun clickDebounce(): Boolean {
        return searchUtils.clickDebounce()
    }

    override fun getSearchRunnable(callback: () -> Unit): Runnable {
        return searchUtils.getSearchRunnable { callback() }
    }

    override fun getEditTextValue(): String {
        return searchUtils.editTextValue
    }

    override fun getLastSearch(): String {
        return searchUtils.lastSearch
    }

    override fun setEditTextValue(value: String) {
        searchUtils.editTextValue = value
    }

    override fun setLastSearch(value: String) {
        searchUtils.lastSearch = value
    }

    override fun isClickAllowed(): Boolean {
        return searchUtils.isClickAllowed
    }

}