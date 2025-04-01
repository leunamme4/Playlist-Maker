package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TracksIntentInteractor
import com.example.playlistmaker.search.domain.api.TracksIntentRepository
import com.example.playlistmaker.search.domain.models.Track

class TracksIntentInteractorImpl(private val tracksIntentRepository: TracksIntentRepository): TracksIntentInteractor {
    override fun setPlayerTrack(track: Track) {
        tracksIntentRepository.setPlayerTrack(track)
    }

    override suspend fun getPlayerTrack(): Track {
        return tracksIntentRepository.getPlayerTrack()
    }
}