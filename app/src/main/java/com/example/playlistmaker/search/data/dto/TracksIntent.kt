package com.example.playlistmaker.search.data.dto

import com.example.playlistmaker.TRACK_PLAYER_KEY
import com.example.playlistmaker.creator.Creator
import com.google.gson.Gson

class TracksIntent {
    private val sharedPreferences = Creator.provideSharedPreferences()

    fun setPlayerTrack(trackDto: TrackDto) {
        sharedPreferences.edit()
            .putString(TRACK_PLAYER_KEY, Gson().toJson(trackDto))
            .apply()
    }

    fun getPlayerTrack(): TrackDto {
        return Gson().fromJson(sharedPreferences.getString(TRACK_PLAYER_KEY, ""), TrackDto::class.java)
    }
}