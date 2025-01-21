package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.search.domain.models.Track

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {
    override fun playControl(
        callback: PlayerInteractor.PlayerConsumer
    ) {
        playerRepository.playControl(
            { callback.consumeControl() },
            { callback.consumeStart() },
            { callback.consumePause() })
    }

    override fun preparePlayer(
        track: Track,
        callbackPrepare: PlayerInteractor.PlayerConsumerPrepare
    ) {
        playerRepository.preparePlayer(track, { callbackPrepare.consumePrepare() })
    }

    override fun releasePlayer() {
        playerRepository.releasePlayer()
    }

    override fun pausePlayer(callbackPause: PlayerInteractor.PlayerPause) {
        playerRepository.pausePlayer({ callbackPause.consumePause() })
    }

    override fun getActualTime() : CharSequence {
        return playerRepository.getActualTime()
    }
}