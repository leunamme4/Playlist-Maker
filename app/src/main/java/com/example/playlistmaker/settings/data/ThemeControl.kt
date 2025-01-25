package com.example.playlistmaker.settings.data

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.THEME


class ThemeControl (private val sharedPreferences: SharedPreferences) {
    private var darkTheme = false

    private fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun themeInit() {
        darkTheme = sharedPreferences.getBoolean(THEME, false)
        switchTheme(darkTheme)
    }

    fun getDarkTheme() : Boolean {
        return sharedPreferences.getBoolean(THEME, false)
    }

    fun onThemeChange(checked: Boolean) {
        switchTheme(checked)
        sharedPreferences.edit()
            .putBoolean(THEME, checked)
            .apply()
    }
}