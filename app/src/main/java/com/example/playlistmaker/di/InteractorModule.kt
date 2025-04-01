package com.example.playlistmaker.di

import com.example.playlistmaker.media.domain.api.FavoritesInteractor
import com.example.playlistmaker.media.domain.impl.FavoritesInteractorImpl
import com.example.playlistmaker.search.domain.api.TracksIntentInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksIntentInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.domain.models.TrackList
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.settings.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.sharing.domain.api.NavigationInteractor
import com.example.playlistmaker.sharing.domain.impl.NavigationInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory {
        TrackList()
    }

    single<TracksIntentInteractor> {
        TracksIntentInteractorImpl(get())
    }

    single<TracksInteractor> {
        TracksInteractorImpl(get(), get())
    }

    single<ThemeInteractor> {
        ThemeInteractorImpl(get())
    }

    single<NavigationInteractor> {
        NavigationInteractorImpl(get())
    }

    single<FavoritesInteractor> {
        FavoritesInteractorImpl(get())
    }
}