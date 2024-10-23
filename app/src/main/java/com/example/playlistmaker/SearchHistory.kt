package com.example.playlistmaker

import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    var historyArrayList: ArrayList<Track> = ArrayList()

    init {
        val historyJson = sharedPreferences.getString(HISTORY_KEY, null)
        val historyArray =
            Gson().fromJson(historyJson, Array<Track>::class.java) ?: emptyArray<Track>()
        historyArrayList.addAll(historyArray)
        historyArrayList.reverse()
    }

    fun updateHistory(track: Track) {
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