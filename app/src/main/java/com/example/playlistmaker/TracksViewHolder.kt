package com.example.playlistmaker

import android.view.RoundedCorner
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val artistNameView: TextView = itemView.findViewById(R.id.artist_name)
    private val trackNameView: TextView = itemView.findViewById(R.id.track_name)
    private val trackTimeView: TextView = itemView.findViewById(R.id.track_time)
    private val trackImageView: ImageView = itemView.findViewById(R.id.track_cover)

    fun bind(track: Track) {
        artistNameView.text = track.artistName
        artistNameView.requestLayout()
        trackNameView.text = track.trackName
        trackTimeView.text = track.trackTimeMillis
        Glide.with(itemView)
            .load(track.artworkUrl100)
            .centerCrop()
            .placeholder(R.drawable.track_cover_placeholder)
            .transform(RoundedCorners(itemView.context.resources.getDimensionPixelSize(R.dimen.cover_corner_radius)))
            .into(trackImageView)
    }
}