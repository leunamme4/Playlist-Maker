package com.example.playlistmaker.domain.models

class TrackList(
    val tracks: ArrayList<Track>
) {
    fun addAllTracks(tracks2: List<Track>) {
        tracks.addAll(tracks2)
    }

    fun clearTracks() {
        tracks.clear()
    }
}