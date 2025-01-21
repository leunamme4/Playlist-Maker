package com.example.playlistmaker.settings.domain.api

interface ThemeRepository {
    fun themeInit()

    fun getDarkTheme() : Boolean

    fun onThemeChange(checked: Boolean)
}