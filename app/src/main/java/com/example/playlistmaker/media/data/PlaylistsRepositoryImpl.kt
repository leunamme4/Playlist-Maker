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

    override suspend fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = PlaylistEntity(
            id = playlist.id,
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

    override suspend fun getPlaylistById(id: Int): Playlist {
        val playlistEntity = database.playlistsDao().getPlaylistById(id)
        val playlist = playlistEntity.let {
            Playlist(
                it.id,
                it.name,
                it.description,
                it.coverPath,
                it.trackListIds,
                it.tracksCount
            )
        }

        return playlist
    }

    override fun getTracksInPlaylist(ids: List<Int>): Flow<List<Track>> = flow {
        emit(
            database.tracksPlaylistDao().getTracks(ids).toMutableList()
                .sortedByDescending { it.addedTime }
                .map { trackConverter.mapInPlaylist(it) })
    }

    override suspend fun deleteTrackById(trackId: Int, playlist: Playlist) {
        val playlists = database.playlistsDao().getPlaylists()

        playlists.forEach {
            val listType: Type = object : TypeToken<List<Int>>() {}.type
            val idsInPlaylist: List<Int>? = Gson().fromJson(it.trackListIds, listType)
            if (idsInPlaylist?.contains(trackId) == true && it.id != playlist.id) {
                val listOfIds = mutableListOf<Int>()
                listOfIds.addAll(idsInPlaylist)
                listOfIds.remove(trackId)
                playlist.tracksCount--
                playlist.trackListIds = Gson().toJson(listOfIds)
                database.playlistsDao().addPlaylist(converter.map(playlist))
                return
            }
        }

        val listOfIds = mutableListOf<Int>()
        val listType: Type = object : TypeToken<List<Int>>() {}.type
        val idsInPlaylist: List<Int>? = Gson().fromJson(playlist.trackListIds, listType)
        if (idsInPlaylist != null) {
            listOfIds.addAll(idsInPlaylist)
            listOfIds.remove(trackId)
        }
        playlist.trackListIds = Gson().toJson(listOfIds)
        playlist.tracksCount--
        database.playlistsDao().addPlaylist(converter.map(playlist))

        database.tracksPlaylistDao().deleteTrackById(trackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val listType: Type = object : TypeToken<List<Int>>() {}.type
        val idsInPlaylist: List<Int>? = Gson().fromJson(playlist.trackListIds, listType)

        idsInPlaylist?.forEach {
            deleteTrackById(it, playlist)
        }

        database.playlistsDao().deletePlaylist(playlist.id)
    }


}