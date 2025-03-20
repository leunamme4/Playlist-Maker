package com.example.playlistmaker.search.domain.models

import android.content.SharedPreferences
import com.example.playlistmaker.TRACK_PLAYER_KEY
import com.google.gson.Gson

class TracksIntent (private val sharedPreferences: SharedPreferences, private val gson: Gson) {

    fun setPlayerTrack(track: Track) {
        sharedPreferences.edit()
            .putString(TRACK_PLAYER_KEY, Gson().toJson(track))
            .apply()
    }

    suspend fun getPlayerTrack(): Track {
        return gson.fromJson(sharedPreferences.getString(TRACK_PLAYER_KEY, ""), Track::class.java)
    }
}