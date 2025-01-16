package com.example.playlistmaker.settings.domain.api

interface ThemeInteractor {
    fun themeInit()

    fun getDarkTheme() : Boolean

    fun onThemeChange(checked: Boolean)
}