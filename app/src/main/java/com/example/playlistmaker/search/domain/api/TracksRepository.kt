package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String) : Flow<Pair<List<Track>, Boolean>>

    fun updateHistory(track: Track)

    fun clearHistory()

    fun getHistory() : List<Track>
}