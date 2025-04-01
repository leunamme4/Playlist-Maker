package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksIntentRepository {
    fun setPlayerTrack(track: Track)

    suspend fun getPlayerTrack(): Track
}