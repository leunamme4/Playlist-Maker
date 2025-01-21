package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.creator.Creator.getThemeInteractor
import com.example.playlistmaker.settings.domain.api.ThemeInteractor

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