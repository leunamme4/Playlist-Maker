package com.example.playlistmaker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks_playlist")
data class TrackPlaylistEntity(
    val trackName: String,
    val artistName: String,
    var trackTimeMillis: String,
    val artworkUrl100: String,
    @PrimaryKey
    val trackId: Int,
    var collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
    var isFavorite: Boolean
)