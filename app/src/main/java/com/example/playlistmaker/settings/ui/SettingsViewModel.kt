package com.example.playlistmaker.settings.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.data.CheckedState
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.sharing.domain.api.NavigationInteractor

class SettingsViewModel (private val themeInteractor: ThemeInteractor, private val navigationInteractor: NavigationInteractor) : ViewModel() {

    private var darkTheme: MutableLiveData<CheckedState> = MutableLiveData()
    fun observeDarkTheme(): LiveData<CheckedState> = darkTheme

    fun initChecked() {
        if (getDarkTheme()) darkTheme.value = CheckedState.Checked else CheckedState.NonChecked
    }

    fun setState(state: CheckedState) {
        darkTheme.value = state
    }

    fun emailIntent() {
        navigationInteractor.emailIntent()
    }

    fun supportIntent() {
        navigationInteractor.supportIntent()
    }

    fun agreementIntent() {
        navigationInteractor.agreementIntent()
    }

    private fun getDarkTheme() : Boolean {
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