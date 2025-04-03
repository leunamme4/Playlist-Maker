package com.example.playlistmaker.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.db.dao.PlaylistsDao
import com.example.playlistmaker.db.dao.TracksDao
import com.example.playlistmaker.db.dao.TracksPlaylistDao
import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.db.entity.TrackEntity
import com.example.playlistmaker.db.entity.TrackPlaylistEntity

@Database(version = 1, entities = [TrackEntity::class, PlaylistEntity::class, TrackPlaylistEntity::class])
abstract class AppDatabase: RoomDatabase() {

    abstract fun TracksDao(): TracksDao

    abstract fun playlistsDao(): PlaylistsDao

    abstract fun tracksPlaylistDao(): TracksPlaylistDao

}