package com.example.playlistmaker.sharing.data

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
}