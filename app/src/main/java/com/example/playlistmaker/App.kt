package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.Creator.getThemeInteractor
import com.example.playlistmaker.domain.api.ThemeInteractor

class App : Application() {

    private lateinit var themeInteractor : ThemeInteractor

    override fun onCreate() {
        super.onCreate()

        Creator.initApplication(this)

        themeInteractor = getThemeInteractor()

        initTheme()
    }

    private fun initTheme() {
        themeInteractor.themeInit()
    }
}