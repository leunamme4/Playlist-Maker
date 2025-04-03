package com.example.playlistmaker.media.ui.playlists

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistsInteractor
import com.example.playlistmaker.media.ui.favorites.FavoritesState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {

    private var isClickAllowed = true

    // state
    private var playlistState = MutableLiveData<PlaylistsState>()
    fun getPlaylistsState(): LiveData<PlaylistsState> = playlistState

    fun getPlaylists() {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect {
                if (it.isEmpty()) playlistState.postValue(PlaylistsState.Empty)
                else playlistState.postValue(PlaylistsState.Content(it))
            }
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