package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.settings.domain.api.ThemeRepository

class ThemeInteractorImpl(private val themeRepository: ThemeRepository) : ThemeInteractor {
    override fun themeInit() {
        themeRepository.themeInit()
    }

    override fun getDarkTheme(): Boolean {
        return themeRepository.getDarkTheme()
    }

    override fun onThemeChange(checked: Boolean) {
        themeRepository.onThemeChange(checked)
    }
}