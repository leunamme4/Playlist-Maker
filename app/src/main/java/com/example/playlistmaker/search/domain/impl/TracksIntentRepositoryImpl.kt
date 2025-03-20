package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.search.domain.api.TracksIntentRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TracksIntent

class TracksIntentRepositoryImpl(private val tracksIntent: TracksIntent, private val database: AppDatabase): TracksIntentRepository {
    override fun setPlayerTrack(track: Track) {
        tracksIntent.setPlayerTrack(track.let { Track(
            it.trackName,
            it.artistName,
            it.trackTimeMillis,
            it.artworkUrl100,
            it.trackId,
            it.collectionName,
            it.releaseDate,
            it.primaryGenreName,
            it.country,
            it.previewUrl,
            it.isFavorite
        ) })
    }

    override suspend fun getPlayerTrack(): Track {
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
            it.previewUrl,
            database.TracksDao().getFavoritesId().contains(it.trackId)
        ) }
    }
}