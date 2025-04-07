package com.example.playlistmaker.sharing.domain.api

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

interface NavigationInteractor {
    fun emailIntent()

    fun supportIntent()

    fun agreementIntent()

    fun playlistShare(tracks: List<Track>, playlist: Playlist)
}