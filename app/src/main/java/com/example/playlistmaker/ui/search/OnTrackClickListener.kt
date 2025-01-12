package com.example.playlistmaker.ui.search

import com.example.playlistmaker.domain.models.Track

fun interface OnTrackClickListener {
    fun onTrackClick(track: Track)
}