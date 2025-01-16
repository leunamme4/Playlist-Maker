package com.example.playlistmaker.creator

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import com.example.playlistmaker.HISTORY_PREFERENCES
import com.example.playlistmaker.PREFERENCES
import com.example.playlistmaker.player.data.Player
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.search.data.SearchUtils
import com.example.playlistmaker.search.data.SearchUtilsRepositoryImpl
import com.example.playlistmaker.settings.data.ThemeControl
import com.example.playlistmaker.settings.data.ThemeRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.dto.SearchHistory
import com.example.playlistmaker.search.domain.models.TrackList
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.domain.api.SearchUtilsInteractor
import com.example.playlistmaker.search.domain.impl.SearchUtilsInteractorImpl
import com.example.playlistmaker.search.domain.api.SearchUtilsRepository
import com.example.playlistmaker.settings.domain.api.ThemeInteractor
import com.example.playlistmaker.settings.domain.api.ThemeRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.settings.domain.impl.ThemeInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.domain.models.Track
import java.util.ArrayList

object Creator {
    private lateinit var application: Application

    fun initApplication(application: Application) {
        Creator.application = application
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