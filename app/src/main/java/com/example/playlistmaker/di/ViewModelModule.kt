package com.example.playlistmaker.di

import com.example.playlistmaker.media.ui.FavoritesViewModel
import com.example.playlistmaker.media.ui.PlaylistsViewModel
import com.example.playlistmaker.player.ui.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchViewModel
import com.example.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        PlayerViewModel(get(), get())
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

    viewModel {
        FavoritesViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }
}