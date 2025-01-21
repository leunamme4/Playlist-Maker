package com.example.playlistmaker.sharing.domain.api

interface NavigationRepository {
    fun emailIntent()

    fun supportIntent()

    fun agreementIntent()
}