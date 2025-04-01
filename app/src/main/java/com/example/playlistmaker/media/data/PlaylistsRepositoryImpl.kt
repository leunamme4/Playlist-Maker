package com.example.playlistmaker.media.data

import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.api.PlaylistsRepository
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.reflect.Type

class PlaylistsRepositoryImpl(
    private val database: AppDatabase,
    private val converter: PlaylistConverter,
    private val trackConverter: TrackConverter
) : PlaylistsRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        val playlistEntity = PlaylistEntity(
            name = playlist.name,
            description = playlist.description,
            coverPath = playlist.coverPath,
            trackListIds = playlist.trackListIds,
            tracksCount = playlist.tracksCount
        )
        database.playlistsDao().addPlaylist(playlistEntity)
    }

    override fun getPlaylists(): Flow<List<Playlist>> = flow {
        emit(database.playlistsDao().getPlaylists().map { converter.map(it) })
    }

    override suspend fun addTrack(track: Track, playlist: Playlist) {
        playlist.tracksCount++

        val listOfIds = mutableListOf<Int>()
        val listType: Type = object : TypeToken<List<Int>>() {}.type
        val idsInPlaylist: List<Int>? = Gson().fromJson(playlist.trackListIds, listType)
        if (idsInPlaylist != null) {
            listOfIds.addAll(idsInPlaylist)
            listOfIds.add(track.trackId)
        } else {
            listOfIds.add(track.trackId)
        }
        playlist.trackListIds = Gson().toJson(listOfIds)

        database.tracksPlaylistDao().addToPlaylist(trackConverter.mapInPlaylist(track))

        database.playlistsDao().addPlaylist(converter.map(playlist))
    }

}