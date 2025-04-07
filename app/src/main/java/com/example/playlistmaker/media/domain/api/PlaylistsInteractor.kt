package com.example.playlistmaker.media.domain.api

import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun addPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    fun getPlaylists(): Flow<List<Playlist>>

    suspend fun addTrackToPlaylist(track: Track, playlist: Playlist)

    suspend fun getPlaylistById(id: Int): Playlist

    fun getTracksInPlaylist(ids: List<Int>): Flow<List<Track>>

    suspend fun deleteTrackById(trackId: Int, playlist: Playlist)

    suspend fun deletePlaylist(playlist: Playlist)
}