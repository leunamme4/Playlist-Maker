package com.example.playlistmaker.search.data.dto

import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.HISTORY_KEY
import com.google.gson.Gson

class SearchHistory {
    private val sharedPreferences = Creator.provideSharedPreferences()
    val historyArrayList: ArrayList<TrackDto> = ArrayList()

    init {
        val historyJson = sharedPreferences.getString(HISTORY_KEY, null)
        val historyArray =
            Gson().fromJson(historyJson, Array<TrackDto>::class.java) ?: emptyArray<TrackDto>()
        historyArrayList.addAll(historyArray)
        historyArrayList.reverse()
    }

    fun updateHistory(track: TrackDto) {
        var exist: Int? = null
        for (track2 in historyArrayList) {
            if (track.trackId == track2.trackId) {
                exist = historyArrayList.indexOf(track2)
            }
        }

        if (exist != null) {
            historyArrayList.removeAt(exist)
        } else if (historyArrayList.size >= 10) {
            historyArrayList.removeLast()
        }
        historyArrayList.add(0, track)

        sharedPreferences.edit()
            .putString(HISTORY_KEY, Gson().toJson(historyArrayList))
            .apply()
    }

    fun clearHistory() {
        historyArrayList.clear()
        sharedPreferences.edit()
            .putString(HISTORY_KEY, Gson().toJson(historyArrayList))
            .apply()
    }
}