package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {

    fun searchTracks(expression: String) : Flow<Pair<List<Track>, Boolean>>

    fun updateHistory(track: Track)

    fun clearHistory(consumer: TracksHistoryConsumer)

    fun getHistory() : List<Track>

    fun getTracks() : ArrayList<Track>

    fun addAllTracks(tracks: List<Track>)

    fun clearTracks()

    interface TracksHistoryConsumer {
        fun consume()
    }
}