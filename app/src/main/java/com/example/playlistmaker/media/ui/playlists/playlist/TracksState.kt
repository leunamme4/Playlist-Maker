package com.example.playlistmaker.media.ui.playlists.playlist

import com.example.playlistmaker.search.domain.models.Track

sealed class TracksState {

    data object Empty : TracksState()

    data class Content(val tracks: List<Track>) : TracksState()
}