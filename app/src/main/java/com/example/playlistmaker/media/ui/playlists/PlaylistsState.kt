package com.example.playlistmaker.media.ui.playlists

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

sealed class PlaylistsState {

    data object Empty : PlaylistsState()

    data class Content(val tracks: List<Playlist>) : PlaylistsState()
}