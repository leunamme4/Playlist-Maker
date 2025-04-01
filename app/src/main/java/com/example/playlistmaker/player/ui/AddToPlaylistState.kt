package com.example.playlistmaker.player.ui

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

sealed class AddToPlaylistState {

    class Added(val playlist: Playlist): AddToPlaylistState()

    class NotAdded(val playlist: Playlist): AddToPlaylistState()
}