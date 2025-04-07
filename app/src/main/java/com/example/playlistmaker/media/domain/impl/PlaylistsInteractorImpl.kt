package com.example.playlistmaker.media.domain.impl

import com.example.playlistmaker.media.domain.api.PlaylistsInteractor
import com.example.playlistmaker.media.domain.api.PlaylistsRepository
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val repository: PlaylistsRepository): PlaylistsInteractor {
    override suspend fun addPlaylist(playlist: Playlist) {
        repository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }

    override fun getPlaylists(): Flow<List<Playlist>> {
        return repository.getPlaylists()
    }

    override suspend fun addTrackToPlaylist(track: Track, playlist: Playlist) {
        repository.addTrack(track, playlist)
    }

    override suspend fun getPlaylistById(id: Int): Playlist {
        return repository.getPlaylistById(id)
    }

    override fun getTracksInPlaylist(ids: List<Int>): Flow<List<Track>> {
        return repository.getTracksInPlaylist(ids)
    }

    override suspend fun deleteTrackById(trackId: Int, playlist: Playlist) {
        repository.deleteTrackById(trackId, playlist)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        repository.deletePlaylist(playlist)
    }
}