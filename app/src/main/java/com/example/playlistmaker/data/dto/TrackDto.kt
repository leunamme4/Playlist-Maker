package com.example.playlistmaker.data.dto

data class TrackDto(
    val trackName: String,
    val artistName: String,
    var trackTimeMillis: String,
    val artworkUrl100: String,
    val trackId: Int,
    var collectionName: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String
) {
}