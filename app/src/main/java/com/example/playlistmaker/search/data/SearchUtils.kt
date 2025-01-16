package com.example.playlistmaker.search.data

import android.os.Handler
import android.os.Looper

class SearchUtils {
    var editTextValue = TEXT_DEFAULT
    var lastSearch = ""
    var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun getSearchRunnable(callback: () -> Unit): Runnable {
        return Runnable {
            lastSearch = editTextValue
            callback()
        }
    }

    companion object {
        private const val TEXT_DEFAULT = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

//    val searchRunnable = Runnable {
//        lastSearch = editTextValue
//        search(editTextValue)
//        searchEditText.clearFocus()
//    }
}