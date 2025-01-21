package com.example.playlistmaker.search.domain.models

class TrackList {

    val tracks: ArrayList<Track> = ArrayList()

    fun addAllTracks(tracks2: List<Track>) {
        tracks.addAll(tracks2)
    }

    fun clearTracks() {
        tracks.clear()
    }
}