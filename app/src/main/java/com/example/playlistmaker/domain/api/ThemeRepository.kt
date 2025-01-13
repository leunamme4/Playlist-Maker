package com.example.playlistmaker.domain.api

interface ThemeRepository {
    fun themeInit()

    fun getDarkTheme() : Boolean

    fun onThemeChange(checked: Boolean)
}