package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.TrackList
import java.util.concurrent.Executors

class TracksInteractorImpl(private val tracksRepository: TracksRepository, private val trackList: TrackList) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            val tracks = tracksRepository.searchTracks(expression)
            consumer.consume(tracks.first, tracks.second)
        }
    }

    override fun updateHistory(track: Track, consumer: TracksInteractor.TracksHistoryConsumer) {
        tracksRepository.updateHistory(track)
        consumer.consume()
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