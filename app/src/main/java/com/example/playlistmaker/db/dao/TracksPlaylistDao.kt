package com.example.playlistmaker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.playlistmaker.db.entity.TrackEntity
import com.example.playlistmaker.db.entity.TrackPlaylistEntity

@Dao
interface TracksPlaylistDao {

    @Insert(entity = TrackPlaylistEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToPlaylist(track: TrackPlaylistEntity)

}