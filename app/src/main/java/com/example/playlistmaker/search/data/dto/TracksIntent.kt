package com.example.playlistmaker.search.data.dto

import android.content.SharedPreferences
import com.example.playlistmaker.TRACK_PLAYER_KEY
import com.google.gson.Gson

class TracksIntent (private val sharedPreferences: SharedPreferences, private val gson: Gson) {

    fun setPlayerTrack(trackDto: TrackDto) {
        sharedPreferences.edit()
            .putString(TRACK_PLAYER_KEY, Gson().toJson(trackDto))
            .apply()
    }

    fun getPlayerTrack(): TrackDto {
        return gson.fromJson(sharedPreferences.getString(TRACK_PLAYER_KEY, ""), TrackDto::class.java)
    }
}