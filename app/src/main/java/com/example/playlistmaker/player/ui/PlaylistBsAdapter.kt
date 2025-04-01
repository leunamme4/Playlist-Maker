package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.ui.playlists.PlaylistsViewHolder

class PlaylistBsAdapter(var playlists: List<Playlist>, private val onClickListener: (playlist: Playlist) -> Unit = {}): RecyclerView.Adapter<PlaylistBsViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistBsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.playlist_bs_view, parent, false)
        return PlaylistBsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlaylistBsViewHolder, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener {
            onClickListener.invoke(playlists[position])
        }
    }
}