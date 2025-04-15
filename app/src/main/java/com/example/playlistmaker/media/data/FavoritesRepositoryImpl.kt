package com.example.playlistmaker.media.data

import android.util.Log
import com.example.playlistmaker.db.AppDatabase
import com.example.playlistmaker.media.domain.api.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesRepositoryImpl(private val database: AppDatabase, private val converter: TrackConverter) :
    FavoritesRepository {
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun addToFavorites(track: Track) {
        database.TracksDao().addToFavorites(converter.map(track))
        saveToFirebase(track)
    }

    override suspend fun deleteFromFavorites(track: Track) {
        database.TracksDao().deleteFromFavorites(converter.map(track))
        deleteFromFirebase(track)
    }

    override fun getFavorites(): Flow<List<Track>> = flow {
        emit(database.TracksDao().getFavorites().sortedByDescending { it.addedTime }
            .map { converter.map(it) })
    }

    private fun saveToFirebase(track: Track) {
        val trackMap = mapOf(
            "trackId" to track.trackId,
            "trackName" to track.trackName,
            "artistName" to track.artistName,
            "trackTimeMillis" to track.trackTimeMillis,
            "artworkUrl100" to track.artworkUrl100,
            "collectionName" to track.collectionName,
            "releaseDate" to track.releaseDate,
            "primaryGenreName" to track.primaryGenreName,
            "country" to track.country,
            "previewUrl" to track.previewUrl,
            "isFavorite" to track.isFavorite
        )

        val uid = FirebaseAuth.getInstance().currentUser?.uid

        try {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid!!)
                .collection("favourites")
                .document(track.trackId.toString())
                .set(trackMap)
                .addOnSuccessListener {
                    Log.d("Firebase_SAVE", "Saved to Firebase: ${track.trackId}")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase_SAVE", "Failed to save to Firebase", e)
                }
        } catch (e: Exception) {
            Log.e("Firebase_SAVE", e.message.toString())
        }
    }

    private fun deleteFromFirebase(track: Track) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("favourites")
                .document(track.trackId.toString())
                .delete()
                .addOnSuccessListener {
                    Log.d("Firebase_DELETE", "Track ${track.trackId} deleted from favourites")
                }
                .addOnFailureListener { e ->
                    Log.e("Firebase_DELETE", "Failed to delete track from favourites", e)
                }
        }
    }

}