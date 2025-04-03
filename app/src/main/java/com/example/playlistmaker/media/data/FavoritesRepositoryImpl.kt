package com.example.playlistmaker.media.data

import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.media.domain.api.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(private val database: AppDatabase, private val converter: TrackConverter) :
    FavoritesRepository {
    override suspend fun addToFavorites(track: Track) {
        database.TracksDao().addToFavorites(converter.map(track))
    }

    override suspend fun deleteFromFavorites(track: Track) {
        database.TracksDao().deleteFromFavorites(converter.map(track))
    }

    override fun getFavorites(): Flow<List<Track>> = flow {
        emit(database.TracksDao().getFavorites().sortedByDescending { it.addedTime }
            .map { converter.map(it) })
    }

}