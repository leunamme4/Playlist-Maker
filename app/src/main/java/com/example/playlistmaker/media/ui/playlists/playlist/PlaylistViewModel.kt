package com.example.playlistmaker.media.ui.playlists.playlist

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.api.PlaylistsInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.api.TracksIntentInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.api.NavigationInteractor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val tracksIntentInteractor: TracksIntentInteractor,
    private val navigationInteractor: NavigationInteractor
) : ViewModel() {

    private var isClickAllowed = true

    private val _deleted = MutableLiveData<Boolean>()
    val deleted: LiveData<Boolean> = _deleted

    private val _toast = MutableLiveData<Boolean>()
    val toast: LiveData<Boolean> = _toast

    private val _playlistInfo = MutableLiveData<Playlist>()
    val playlistInfo: LiveData<Playlist> = _playlistInfo

    private val _tracksState = MutableLiveData<TracksState>()
    val tracksState: LiveData<TracksState> = _tracksState

    private val _generalTime = MutableLiveData<String>()
    val generalTime: LiveData<String> = _generalTime

    fun getPlaylistInfo(id: Int) {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(id)
            _playlistInfo.postValue(playlist)
            getTracks(playlist)
        }
    }

    private suspend fun getTracks(playlist: Playlist) {
        if (playlist.tracksCount == 0) {
            _tracksState.postValue(TracksState.Empty)
            _generalTime.postValue("0 минут")
        } else {
            val listType: Type = object : TypeToken<List<Int>>() {}.type
            val idsInPlaylist: List<Int> = Gson().fromJson(playlist.trackListIds, listType)
            playlistsInteractor.getTracksInPlaylist(idsInPlaylist).collect {
                var generalTime = 0
                for (track in it) {
                    val timeMmSs = track.trackTimeMillis
                    val parts = timeMmSs.split(":")
                    val minutes = parts[0].toInt()
                    val millis = parts[1].toInt() * 1000 + minutes * 60 * 1000
                    generalTime += millis
                }
                var formatted = SimpleDateFormat("mm", Locale.getDefault()).format(generalTime) + " минут"
                _generalTime.postValue(formatted)
                _tracksState.postValue(TracksState.Content(it))
            }
        }
    }

    fun deleteTrack(track: Track) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrackById(track.trackId, playlistInfo.value!!)
            getPlaylistInfo(playlistInfo.value!!.id)
            getTracks(playlistInfo.value!!)
        }

    }

    fun deletePlaylist() {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(playlistInfo.value!!)
            _deleted.postValue(true)
        }
    }


    @SuppressLint("SuspiciousIndentation")
    fun sharePlaylist() {
        if (playlistInfo.value != null && tracksState.value is TracksState.Content) {
        navigationInteractor.playlistShare((tracksState.value as TracksState.Content).tracks, playlistInfo.value!!) } else {
            _toast.postValue(true)
            _toast.postValue(false)
        }
    }



    fun trackTransition(track: Track) {
        if (clickDebounce()) {
            tracksIntentInteractor.setPlayerTrack(track)
        }
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }


}