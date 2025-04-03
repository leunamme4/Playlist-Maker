package com.example.playlistmaker.sharing.domain.impl

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.api.NavigationInteractor
import com.example.playlistmaker.sharing.domain.api.NavigationRepository

class NavigationInteractorImpl (private val navigationRepository: NavigationRepository) : NavigationInteractor {
    override fun emailIntent() {
        navigationRepository.emailIntent()
    }

    override fun supportIntent() {
        navigationRepository.supportIntent()
    }

    override fun agreementIntent() {
        navigationRepository.agreementIntent()
    }

    override fun playlistShare(tracks: List<Track>, playlist: Playlist) {
        navigationRepository.playlistShare(tracks, playlist)
    }
}