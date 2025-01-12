package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface PlayerInteractor {
    fun playControl(callback: PlayerConsumer)

    fun preparePlayer(track: Track, callbackPrepare: PlayerConsumerPrepare)

    fun releasePlayer()

    fun pausePlayer(callbackPause: PlayerPause)

    fun getActualTime() : CharSequence

    interface PlayerConsumerPrepare {
        fun consumePrepare()
    }

    interface PlayerConsumer {
        fun consumeStart()

        fun consumePause()

        fun consumeControl()
    }

    interface PlayerPause {
        fun consumePause()
    }
}