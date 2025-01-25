package com.example.playlistmaker.di

import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.data.SearchUtilsRepositoryImpl
import com.example.playlistmaker.search.data.TracksIntentRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchUtilsRepository
import com.example.playlistmaker.search.domain.api.TracksIntentRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.api.ThemeRepository
import com.example.playlistmaker.sharing.data.NavigationRepositoryImpl
import com.example.playlistmaker.sharing.domain.api.NavigationRepository
import org.koin.dsl.module


val repositoryModule = module {

    single<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    single<SearchUtilsRepository> {
        SearchUtilsRepositoryImpl(get())
    }

    single<TracksIntentRepository> {
        TracksIntentRepositoryImpl(get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    single<ThemeRepository> {
        ThemeRepositoryImpl(get())
    }

    single<NavigationRepository> {
        NavigationRepositoryImpl(get())
    }
}