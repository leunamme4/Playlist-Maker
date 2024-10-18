package com.example.playlistmaker

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter (private val searchHistory: SearchHistory) : RecyclerView.Adapter<TracksViewHolder> () {
    private val tracks = searchHistory.historyArrayList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TracksViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        holder.bind(tracks[position])
        holder.itemView.setOnClickListener {
            searchHistory.updateHistory(tracks[position])
            notifyDataSetChanged()
        }
    }

}