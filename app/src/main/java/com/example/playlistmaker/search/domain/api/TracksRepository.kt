package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String) : Pair<List<Track>, Boolean>

    fun updateHistory(track: Track)

    fun clearHistory()

    fun getHistory() : List<Track>
}