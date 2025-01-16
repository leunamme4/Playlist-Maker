package com.example.playlistmaker.domain.api

interface ThemeInteractor {
    fun themeInit()

    fun getDarkTheme() : Boolean

    fun onThemeChange(checked: Boolean)
}