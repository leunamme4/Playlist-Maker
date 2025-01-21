package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.dto.TracksIntent
import com.example.playlistmaker.search.domain.api.TracksIntentRepository
import com.example.playlistmaker.search.domain.models.Track

class TracksIntentRepositoryImpl(private val tracksIntent: TracksIntent): TracksIntentRepository {
    override fun setPlayerTrack(track: Track) {
        tracksIntent.setPlayerTrack(track.let { TrackDto(
            it.trackName,
            it.artistName,
            it.trackTimeMillis,
            it.artworkUrl100,
            it.trackId,
            it.collectionName,
            it.releaseDate,
            it.primaryGenreName,
            it.country,
            it.previewUrl
        ) })
    }

    override fun getPlayerTrack(): Track {
        return tracksIntent.getPlayerTrack().let { Track(
            it.trackName,
            it.artistName,
            it.trackTimeMillis,
            it.artworkUrl100,
            it.trackId,
            it.collectionName,
            it.releaseDate,
            it.primaryGenreName,
            it.country,
            it.previewUrl
        ) }
    }
}