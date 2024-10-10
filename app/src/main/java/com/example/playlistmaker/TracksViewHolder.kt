package com.example.playlistmaker

import android.view.RoundedCorner
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TracksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val artistNameView: TextView = itemView.findViewById(R.id.artist_name)
    val trackNameView: TextView = itemView.findViewById(R.id.track_name)
    val trackTimeView: TextView = itemView.findViewById(R.id.track_time)
    val trackImageView: ImageView = itemView.findViewById(R.id.track_cover)

    fun bind(track: Track) {
        artistNameView.text = track.artistName
        trackNameView.text = track.trackName
        trackTimeView.text = track.trackTime
        Glide.with(itemView).load(track.artworkUrl100).centerCrop().placeholder(R.drawable.track_cover_placeholder).transform(RoundedCorners(itemView.context.resources.getDimensionPixelSize(R.dimen.cover_corner_radius))).into(trackImageView)
    }
}