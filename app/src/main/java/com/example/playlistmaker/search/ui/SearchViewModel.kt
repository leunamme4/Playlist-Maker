package com.example.playlistmaker.search.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.TracksIntentInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val tracksIntentInteractor: TracksIntentInteractor
) : ViewModel() {

    private var tracks = getTrackList()

    private var history = getTracksHistory()

    private var searchJob: Job? = null

    private var editTextValue = TEXT_DEFAULT
    private var lastSearch = ""
    private var isClickAllowed = true

    // state
    private var searchState = MutableLiveData<SearchState>()
    fun getSearchState(): LiveData<SearchState> = searchState

    fun setState(searchState: SearchState) {
        this.searchState.value = searchState
    }

    fun setEditTextValue(text: String) {
        editTextValue = text
    }

    fun search(text: String) {
        val searchText = if (text == "") lastSearch else text
        setState(SearchState.Loading)
        lastSearch = searchText
        tracksInteractor.clearTracks()

        viewModelScope.launch {
            tracksInteractor.searchTracks(searchText)
                .collect { pair ->
                    if (!pair.second) { setState(SearchState.NoConnection) }
                    else if (pair.first.isNotEmpty()) {
                        tracksInteractor.addAllTracks(pair.first)
                        setState(SearchState.Content(tracks))
                    } else {
                        setState(SearchState.Empty)
                    }
                }
        }
    }

    private fun getTracksHistory(): List<Track> {
        return tracksInteractor.getHistory()
    }

    private fun getTrackList(): ArrayList<Track> {
        return tracksInteractor.getTracks()
    }

    fun clearHistory() {
        tracksInteractor.clearHistory(object : TracksInteractor.TracksHistoryConsumer {
            override fun consume() {
                history = getTracksHistory()
                setState(SearchState.Default)
            }
        }
        )
    }

    fun trackTransition(track: Track) {
        tracksIntentInteractor.setPlayerTrack(track)
    }

    fun updateHistory(track: Track) {
        if (clickDebounce()) {
            tracksInteractor.updateHistory(track)
            history = getTracksHistory()
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

    fun searchTask() {
        cancelSearch()
        searchJob = viewModelScope.launch {
            lastSearch = editTextValue
            delay(SEARCH_DEBOUNCE_DELAY_MILLIS)
            search(editTextValue)
        }
    }

    fun cancelSearch() {
        searchJob?.cancel()
    }

    fun setHistoryState() {
        setState(SearchState.History(history))
    }


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
        private const val TEXT_DEFAULT = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}