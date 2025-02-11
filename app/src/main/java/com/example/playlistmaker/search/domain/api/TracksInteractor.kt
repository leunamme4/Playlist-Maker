package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksInteractor {

    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun updateHistory(track: Track)

    fun clearHistory(consumer: TracksHistoryConsumer)

    fun getHistory() : List<Track>

    fun getTracks() : ArrayList<Track>

    fun addAllTracks(tracks: List<Track>)

    fun clearTracks()

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>, status: Boolean)
    }

    interface TracksHistoryConsumer {
        fun consume()
    }
}