package com.example.playlistmaker.di

import com.example.playlistmaker.media.data.FavoritesRepositoryImpl
import com.example.playlistmaker.media.domain.api.FavoritesRepository
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.TracksIntentRepository
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.TracksIntentRepositoryImpl
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.domain.api.ThemeRepository
import com.example.playlistmaker.sharing.data.NavigationRepositoryImpl
import com.example.playlistmaker.sharing.domain.api.NavigationRepository
import org.koin.dsl.module


val repositoryModule = module {

    single<TracksIntentRepository> {
        TracksIntentRepositoryImpl(get(), get())
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

    single<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }
}