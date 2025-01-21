package com.example.playlistmaker.search.ui

sealed class SearchState {
    data object Default: SearchState()

    data object Loading : SearchState()

    data object Content : SearchState()

    data object History: SearchState()

    data object NoConnection: SearchState()

    data object Empty : SearchState()
}