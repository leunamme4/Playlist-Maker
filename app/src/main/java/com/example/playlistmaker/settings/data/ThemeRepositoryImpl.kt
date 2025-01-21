package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.domain.api.ThemeRepository

class ThemeRepositoryImpl(private val themeControl: ThemeControl): ThemeRepository {
    override fun themeInit() {
        themeControl.themeInit()
    }

    override fun getDarkTheme(): Boolean {
        return themeControl.getDarkTheme()
    }

    override fun onThemeChange(checked: Boolean) {
        themeControl.onThemeChange(checked)
    }
}