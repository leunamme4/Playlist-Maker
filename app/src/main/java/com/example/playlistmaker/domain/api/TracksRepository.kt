package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String) : Pair<List<Track>, Boolean>

    fun updateHistory(track: Track)

    fun clearHistory()

    fun getHistory() : List<Track>
}