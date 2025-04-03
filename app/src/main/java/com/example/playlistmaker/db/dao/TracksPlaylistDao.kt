package com.example.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.entity.TrackEntity
import com.example.playlistmaker.db.entity.TrackPlaylistEntity

@Dao
interface TracksPlaylistDao {

    @Insert(entity = TrackPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToPlaylist(track: TrackPlaylistEntity)

    @Query("DELETE FROM tracks_playlist WHERE trackId == :trackId")
    suspend fun deleteTrackById(trackId: Int)

    @Query("SELECT * FROM tracks_playlist WHERE trackId IN (:ids)")
    suspend fun getTracks(ids: List<Int>): List<TrackPlaylistEntity>
}