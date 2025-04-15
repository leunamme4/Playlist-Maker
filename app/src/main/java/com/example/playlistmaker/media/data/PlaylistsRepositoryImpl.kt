package com.example.playlistmaker.media.data

import android.util.Log
import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.media.domain.api.PlaylistsRepository
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

        savePlaylistToFirebase(playlistEntity)
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
        deletePlaylistFromFirebase(converter.map(playlist))
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
        savePlaylistToFirebase(converter.map(playlist))
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
        deletePlaylistFromFirebase(converter.map(playlist))
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
        savePlaylistToFirebase(converter.map(playlist))
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val listType: Type = object : TypeToken<List<Int>>() {}.type
        val idsInPlaylist: List<Int>? = Gson().fromJson(playlist.trackListIds, listType)

        idsInPlaylist?.forEach {
            deleteTrackById(it, playlist)
        }

        database.playlistsDao().deletePlaylist(playlist.id)
        deletePlaylistFromFirebase(converter.map(playlist))
    }


    private fun savePlaylistToFirebase(playlist: PlaylistEntity) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        val playlistMap = mapOf(
            "id" to playlist.id,
            "name" to playlist.name,
            "description" to playlist.description,
            "coverPath" to playlist.coverPath,
            "trackListIds" to playlist.trackListIds,
            "tracksCount" to playlist.tracksCount
        )

        try {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid!!)
                .collection("playlists")
                .document(playlist.id.toString()) // ID как строка в Firestore
                .set(playlistMap)
                .addOnSuccessListener {
                    Log.d("Firebase_SAVE", "Playlist saved: ${playlist.name}")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase_SAVE", "Failed to save playlist", e)
                }
        } catch (e: Exception) {
            Log.e("Firebase_SAVE", "Exception: ${e.message}")
        }
    }

    private fun deletePlaylistFromFirebase(playlist: PlaylistEntity) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("playlists")
                .document(playlist.id.toString())
                .delete()
                .addOnSuccessListener {
                    Log.d("Firebase_DELETE", "Playlist deleted: ${playlist.name}")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase_DELETE", "Failed to delete playlist", e)
                }
        }
    }


}