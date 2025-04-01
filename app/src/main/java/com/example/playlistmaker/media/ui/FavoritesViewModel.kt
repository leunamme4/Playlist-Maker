package com.example.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.FavoritesInteractor
import com.example.playlistmaker.search.domain.api.TracksIntentInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val favoritesInteractor: FavoritesInteractor,
    private val tracksIntentInteractor: TracksIntentInteractor
) : ViewModel() {

    private var isClickAllowed = true

    // state
    private var favoritesState = MutableLiveData<FavoritesState>()
    fun getFavoritesState(): LiveData<FavoritesState> = favoritesState

    fun trackTransition(track: Track) {
        if (clickDebounce()) {
            tracksIntentInteractor.setPlayerTrack(track)
        }
    }

    fun getFavorites() {
        viewModelScope.launch {
            favoritesInteractor.getFavorites().collect {
                if (it.isEmpty()) favoritesState.postValue(FavoritesState.Empty)
                else favoritesState.postValue(FavoritesState.Content(it))
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