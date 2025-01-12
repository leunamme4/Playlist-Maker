package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksRepository {
    fun searchTracks(expression: String) : Pair<List<Track>, Boolean>

    fun updateHistory(track: Track)

    fun clearHistory()

    fun getHistory() : List<Track>

//    fun getTracks() : ArrayList<Track>
//
//    fun addAllTracks(tracks: List<Track>)
//
//    fun clearTracks()
}