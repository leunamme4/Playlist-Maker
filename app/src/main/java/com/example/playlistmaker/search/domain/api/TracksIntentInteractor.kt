package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface TracksIntentInteractor {
    fun setPlayerTrack(track: Track)

    fun getPlayerTrack(): Track
}