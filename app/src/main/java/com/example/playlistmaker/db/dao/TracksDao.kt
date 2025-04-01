package com.example.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.entity.TrackEntity

@Dao
interface TracksDao {
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(track: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteFromFavorites(track: TrackEntity)

    @Query("SELECT * FROM tracks_favorite")
    suspend fun getFavorites(): List<TrackEntity>

    @Query("SELECT trackId FROM tracks_favorite")
    suspend fun getFavoritesId(): List<Int>
}