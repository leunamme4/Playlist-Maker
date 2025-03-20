package com.example.playlistmaker.media.ui

import com.example.playlistmaker.search.domain.models.Track

sealed class FavoritesState {

    data object Empty : FavoritesState()

    data class Content(val tracks: List<Track>) : FavoritesState()
}