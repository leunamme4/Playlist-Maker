package com.example.playlistmaker.sharing.data

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.api.NavigationRepository

class NavigationRepositoryImpl (private val navigator: ExternalNavigator) : NavigationRepository {
    override fun emailIntent() {
        navigator.emailIntent()
    }

    override fun supportIntent() {
        navigator.supportIntent()
    }

    override fun agreementIntent() {
        navigator.agreementIntent()
    }

    override fun playlistShare(tracks: List<Track>, playlist: Playlist) {
        navigator.playlistShare(tracks, playlist)
    }
}