package com.example.playlistmaker.media.domain.models


data class Playlist(
    val id: Int = 0,
    var name: String = "",
    var description: String = "",
    var coverPath: String? = null,
    var trackListIds: String = "",
    var tracksCount: Int = 0
)