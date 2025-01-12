package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface PlayerRepository {

    fun playControl(callbackControl: () -> Unit, callbackStart: () -> Unit, callbackPause: () -> Unit)

    fun preparePlayer(track: Track, callbackPrepare: () -> Unit)

    fun releasePlayer()

    fun pausePlayer(callbackPause: () -> Unit)

    fun getActualTime() : CharSequence

}