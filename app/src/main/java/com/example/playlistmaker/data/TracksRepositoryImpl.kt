package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.SearchHistory
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.domain.models.TrackList
import com.example.playlistmaker.data.dto.TracksSearchRequest
import com.example.playlistmaker.data.dto.TracksSearchResponse
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val history: SearchHistory
) : TracksRepository {

    override fun searchTracks(expression: String): Pair<List<Track>, Boolean> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        if (response.resultCode == 200) {
            return Pair((response as TracksSearchResponse).results.map {
                it.trackTimeMillis = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(it.trackTimeMillis.toLongOrNull())
                Track(
                    it.trackName,
                    it.artistName,
                    it.trackTimeMillis,
                    it.artworkUrl100,
                    it.trackId,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }, true)
        } else {
            return Pair(emptyList(), false)
        }
    }

    override fun updateHistory(track: Track) {
        history.updateHistory(
            TrackDto(
                track.trackName,
                track.artistName,
                track.trackTimeMillis,
                track.artworkUrl100,
                track.trackId,
                track.collectionName,
                track.releaseDate,
                track.primaryGenreName,
                track.country,
                track.previewUrl
            )
        )
    }

    override fun clearHistory() {
        history.clearHistory()
    }

    override fun getHistory(): List<Track> {
        return history.historyArrayList.map {
            Track(
                it.trackName,
                it.artistName,
                it.trackTimeMillis,
                it.artworkUrl100,
                it.trackId,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
    }

//    override fun getTracks(): ArrayList<Track> {
//        return trackList.tracks
//    }
//
//    override fun addAllTracks(tracks: List<Track>) {
//        trackList.addAllTracks(tracks)
//    }
//
//    override fun clearTracks() {
//        trackList.clearTracks()
//    }
}