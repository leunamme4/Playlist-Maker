package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TrackList
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository,
    private val trackList: TrackList
) :
    TracksInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>, Boolean>> {
        return tracksRepository.searchTracks(expression)
    }

    override fun updateHistory(track: Track) {
        tracksRepository.updateHistory(track)
    }

    override fun clearHistory(consumer: TracksInteractor.TracksHistoryConsumer) {
        tracksRepository.clearHistory()
        consumer.consume()
    }

    override fun getHistory(): List<Track> {
        return tracksRepository.getHistory()
    }

    override fun getTracks(): ArrayList<Track> {
        return trackList.tracks
    }

    override fun addAllTracks(tracks: List<Track>) {
        trackList.addAllTracks(tracks)
    }

    override fun clearTracks() {
        trackList.clearTracks()
    }

}