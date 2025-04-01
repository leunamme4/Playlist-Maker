package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.api.FavoritesInteractor
import com.example.playlistmaker.media.domain.api.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(private val favoritesRepository: FavoritesRepository): FavoritesInteractor {
    override suspend fun addToFavorites(track: Track) {
        favoritesRepository.addToFavorites(track)
    }

    override suspend fun deleteFromFavorites(track: Track) {
        favoritesRepository.deleteFromFavorites(track)
    }

    override fun getFavorites(): Flow<List<Track>> {
        return favoritesRepository.getFavorites()
    }
}