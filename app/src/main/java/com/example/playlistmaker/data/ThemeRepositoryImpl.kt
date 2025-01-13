package com.example.playlistmaker.data

import com.example.playlistmaker.domain.api.ThemeRepository

class ThemeRepositoryImpl(val themeControl: ThemeControl): ThemeRepository {
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