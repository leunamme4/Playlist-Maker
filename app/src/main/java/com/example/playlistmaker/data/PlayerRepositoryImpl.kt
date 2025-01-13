package com.example.playlistmaker.data

import com.example.playlistmaker.domain.api.PlayerRepository
import com.example.playlistmaker.domain.models.Track

class PlayerRepositoryImpl(private val player: Player) : PlayerRepository {

    override fun playControl(callbackControl: () -> Unit, callbackStart: () -> Unit, callbackPause: () -> Unit) {
        player.playControl({ callbackControl() }, { callbackStart() }, {callbackPause()})
    }

    override fun preparePlayer(track: Track, callbackPrepare: () -> Unit) {
        player.preparePlayer(track, callbackPrepare)
    }

    override fun releasePlayer() {
        player.releasePlayer()
    }

    override fun pausePlayer(callbackPause: () -> Unit) {
        player.pausePlayer(callbackPause)
    }

    override fun getActualTime(): CharSequence {
        return player.actualTime()
    }

}