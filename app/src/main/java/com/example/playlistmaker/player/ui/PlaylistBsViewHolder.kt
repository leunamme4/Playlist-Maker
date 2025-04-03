package com.example.playlistmaker.player.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.Playlist

class PlaylistBsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private var cover: ImageView = itemView.findViewById(R.id.track_cover)
    private var name: TextView = itemView.findViewById(R.id.playlist_name)
    private var trackCount: TextView = itemView.findViewById(R.id.tracks_count)

    fun bind(playlist: Playlist) {
        name.text = playlist.name
        trackCount.text = "${playlist.tracksCount} ${trackCountFormat(playlist.tracksCount)}"
        Glide.with(itemView)
            .load(playlist.coverPath)
            .centerCrop()
            .placeholder(R.drawable.track_cover_placeholder)
            .transform(RoundedCorners(itemView.context.resources.getDimensionPixelSize(R.dimen.playlist_cover_corner_radius)))
            .into(cover)
    }

    private fun trackCountFormat(count: Int): String {
        return when(count % 10) {
            0, in 5..9 -> "треков"
            1 -> "трек"
            in 2..4 -> "трека"
            else -> {""}
        }
    }
}