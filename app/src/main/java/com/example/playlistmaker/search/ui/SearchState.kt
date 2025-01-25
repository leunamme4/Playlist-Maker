package com.example.playlistmaker.search.ui

import com.example.playlistmaker.search.domain.models.Track

sealed class SearchState {
    data object Default: SearchState()

    data object Loading : SearchState()

    data class Content(val tracks: List<Track>) : SearchState()

    data class History(val tracks: List<Track>): SearchState()

    data object NoConnection: SearchState()

    data object Empty : SearchState()
}