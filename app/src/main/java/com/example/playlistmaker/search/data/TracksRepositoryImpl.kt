package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.data.dto.SearchHistory
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import com.example.playlistmaker.search.data.dto.TracksSearchResponse
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val history: SearchHistory
) : TracksRepository {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>, Boolean>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        if (response.resultCode == 200) {
            emit(Pair((response as TracksSearchResponse).results.map {
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
            }, true))
        } else {
            emit(Pair(emptyList(), false))
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
}