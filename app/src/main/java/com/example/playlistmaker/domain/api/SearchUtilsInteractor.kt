package com.example.playlistmaker.domain.api

interface SearchUtilsInteractor {
    fun clickDebounce(): Boolean

    fun getSearchRunnable(callback: SearchRunnableConsume): Runnable

    fun getEditTextValue() : String

    fun getLastSearch() : String

    fun isClickAllowed() : Boolean

    fun setEditTextValue(value: String)

    fun setLastSearch(value: String)

    interface SearchRunnableConsume {
        fun consume()
    }
}