package com.example.playlistmaker.sharing.domain.impl

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
}