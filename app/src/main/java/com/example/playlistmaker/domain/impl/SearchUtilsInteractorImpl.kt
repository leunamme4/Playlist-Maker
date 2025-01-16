package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SearchUtilsInteractor
import com.example.playlistmaker.domain.api.SearchUtilsRepository

class SearchUtilsInteractorImpl(private val searchUtilsRepository: SearchUtilsRepository):
    SearchUtilsInteractor {
    override fun clickDebounce(): Boolean {
        return searchUtilsRepository.clickDebounce()
    }

    override fun getSearchRunnable(callback: SearchUtilsInteractor.SearchRunnableConsume): Runnable {
        return searchUtilsRepository.getSearchRunnable { callback.consume() }
    }

    override fun getEditTextValue(): String {
        return searchUtilsRepository.getEditTextValue()
    }

    override fun getLastSearch(): String {
        return searchUtilsRepository.getLastSearch()
    }

    override fun isClickAllowed(): Boolean {
        return searchUtilsRepository.isClickAllowed()
    }

    override fun setEditTextValue(value: String) {
        searchUtilsRepository.setEditTextValue(value)
    }

    override fun setLastSearch(value: String) {
        searchUtilsRepository.setLastSearch(value)
    }
}