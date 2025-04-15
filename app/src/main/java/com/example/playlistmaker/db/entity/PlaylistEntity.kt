package com.example.playlistmaker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID
import kotlin.random.Random

@Entity(tableName = "playlists")
data class PlaylistEntity (
    //@PrimaryKey(autoGenerate = true)
    @PrimaryKey
    val id: Int = Random.nextInt(1000000),
    var name: String,
    var description: String,
    var coverPath: String?,
    var trackListIds: String = "",
    var tracksCount: Int
)