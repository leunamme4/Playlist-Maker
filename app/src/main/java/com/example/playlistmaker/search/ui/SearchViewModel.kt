package com.example.playlistmaker.search.ui

import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.api.SearchUtilsInteractor
import com.example.playlistmaker.search.domain.api.TracksIntentInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    private val searchUtilsInteractor: SearchUtilsInteractor,
    private val tracksIntentInteractor: TracksIntentInteractor,
    private val handler: Handler
) : ViewModel() {

    private var tracks = getTrackList()

    private var history = getTracksHistory()

    // state
    private var searchState = MutableLiveData<SearchState>()
    fun getSearchState(): LiveData<SearchState> = searchState

    fun setState(searchState: SearchState) {
        this.searchState.value = searchState
    }

    fun setEditTextValue(text: String) {
        searchUtilsInteractor.setEditTextValue(text)
    }

    private fun getLastSearch(): String {
        return searchUtilsInteractor.getLastSearch()
    }

    private fun setLastSearch(text: String) {
        searchUtilsInteractor.setLastSearch(text)
    }

    fun search(text: String) {
        val searchText = if (text == "") getLastSearch() else text
        setState(SearchState.Loading)
        setLastSearch(searchText)
        tracksInteractor.clearTracks()

        tracksInteractor.searchTracks(searchText, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>, status: Boolean) {
                tracksInteractor.clearTracks()
                if (!status) {
                    handler.post { setState(SearchState.NoConnection) }
                } else if (foundTracks.isNotEmpty()) {
                    tracksInteractor.addAllTracks(foundTracks)
                    handler.post { setState(SearchState.Content(tracks)) }
                } else {
                    handler.post { setState(SearchState.Empty) }
                }
            }
        })
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
        return searchUtilsInteractor.clickDebounce()
    }

    private val searchRunnable = searchUtilsInteractor.getSearchRunnable(object :
        SearchUtilsInteractor.SearchRunnableConsume {
        override fun consume() {
            search(searchUtilsInteractor.getEditTextValue())
        }

    })

    fun runnableTask() {
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY_MILLIS)
    }

    fun removeCallbacks() {
        handler.removeCallbacks(searchRunnable)
    }

    fun setHistoryState() {
        setState(SearchState.History(history))
    }


    companion object {
        private const val SEARCH_DEBOUNCE_DELAY_MILLIS = 2000L
    }
}