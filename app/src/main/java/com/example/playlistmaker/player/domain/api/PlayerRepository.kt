package com.example.playlistmaker.player.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface PlayerRepository {

    fun playControl(callbackControl: () -> Unit, callbackStart: () -> Unit, callbackPause: () -> Unit)

    fun preparePlayer(track: Track, callbackPrepare: () -> Unit)

    fun releasePlayer()

    fun pausePlayer(callbackPause: () -> Unit)

    fun getActualTime() : CharSequence

}