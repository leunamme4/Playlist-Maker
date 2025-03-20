package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.playlistmaker.PREFERENCES
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.SearchHistory
import com.example.playlistmaker.search.data.dto.TracksIntent
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.network.TracksApiService
import com.example.playlistmaker.settings.data.ThemeControl
import com.example.playlistmaker.sharing.data.ExternalNavigator
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<TracksApiService> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TracksApiService::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    }

    factory {
        Gson()
    }

    factory {
        MediaPlayer()
    }

    factory {
        Response()
    }

    factory {
        Runnable { }
    }

    single {
        Handler(Looper.getMainLooper())
    }

    single {
        SearchHistory(get(), get())
    }

    factory {
        TracksIntent(get(), get())
    }

    single {
        ThemeControl(get())
    }

    single {
        ExternalNavigator(androidContext())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }


}