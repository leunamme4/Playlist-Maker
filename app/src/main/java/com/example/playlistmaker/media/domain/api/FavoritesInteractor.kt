package com.example.playlistmaker.media.domain.api

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    suspend fun addToFavorites(track: Track)

    suspend fun deleteFromFavorites(track: Track)

    fun getFavorites(): Flow<List<Track>>
}