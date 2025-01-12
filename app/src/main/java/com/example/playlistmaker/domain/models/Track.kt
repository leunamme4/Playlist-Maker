package com.example.playlistmaker.domain.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Track(
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
) : Parcelable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    fun getYear() = releaseDate.takeWhile { it != '-' }
    }
