package com.example.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.playlistmaker.data.Player
import com.example.playlistmaker.data.PlayerRepositoryImpl
import com.example.playlistmaker.data.SearchUtils
import com.example.playlistmaker.data.SearchUtilsRepositoryImpl
import com.example.playlistmaker.data.ThemeControl
import com.example.playlistmaker.data.ThemeRepositoryImpl
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.dto.SearchHistory
import com.example.playlistmaker.domain.models.TrackList
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.api.SearchUtilsInteractor
import com.example.playlistmaker.domain.api.SearchUtilsInteractorImpl
import com.example.playlistmaker.domain.api.SearchUtilsRepository
import com.example.playlistmaker.domain.api.ThemeInteractor
import com.example.playlistmaker.domain.api.ThemeRepository
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.models.Track
import java.util.ArrayList

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        this.application = application
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(), SearchHistory())
    }

    fun getTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(), TrackList(ArrayList<Track>()))
    }

    fun provideSharedPreferences() : SharedPreferences {
        return application.getSharedPreferences(HISTORY_PREFERENCES, MODE_PRIVATE)
    }

    fun provideThemeSharedPreferences() : SharedPreferences {
        return application.getSharedPreferences(PREFERENCES, MODE_PRIVATE)
    }

    fun getPlayerInteractor() : PlayerInteractor {
        return PlayerInteractorImpl(getPlayerRepository())
    }

    private fun getPlayerRepository() : PlayerRepository {
        return PlayerRepositoryImpl(Player())
    }

    fun getThemeInteractor() : ThemeInteractor {
        return ThemeInteractorImpl(getThemeRepository())
    }

    private fun getThemeRepository() : ThemeRepository {
        return ThemeRepositoryImpl(ThemeControl())
    }

    fun getSearchUtilsInteractor() : SearchUtilsInteractor {
        return SearchUtilsInteractorImpl(getSearchUtilsRepository())
    }

    private fun getSearchUtilsRepository() : SearchUtilsRepository {
        return SearchUtilsRepositoryImpl(SearchUtils())
    }
}