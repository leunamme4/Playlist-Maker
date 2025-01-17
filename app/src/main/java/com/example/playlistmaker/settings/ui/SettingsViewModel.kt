package com.example.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.sharing.domain.api.NavigationInteractor

class SettingsViewModel (private val themeInteractor: ThemeInteractor, private val navigationInteractor: NavigationInteractor) : ViewModel() {

    fun emailIntent() {
        navigationInteractor.emailIntent()
    }

    fun supportIntent() {
        navigationInteractor.supportIntent()
    }

    fun agreementIntent() {
        navigationInteractor.agreementIntent()
    }

    fun getDarkTheme() : Boolean {
        return themeInteractor.getDarkTheme()
    }

    fun onThemeChanged(checked: Boolean) {
        themeInteractor.onThemeChange(checked)
    }

    companion object {
        fun getViewModelFactory() : ViewModelProvider.Factory = viewModelFactory{
            initializer {
                val themeInteractor = Creator.getThemeInteractor()
                val navigationInteractor = Creator.getNavigationInteractor()

                SettingsViewModel(themeInteractor, navigationInteractor)
            }
        }
    }
}