package com.example.playlistmaker.player.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.api.TracksIntentInteractor
import com.example.playlistmaker.search.domain.models.Track

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val tracksIntentInteractor: TracksIntentInteractor
) : ViewModel() {

    private var track = MutableLiveData<Track>(tracksIntentInteractor.getPlayerTrack())

    fun observeTrack(): LiveData<Track> = track

    private var playerScreenState = MutableLiveData<PlayerState>(PlayerState.Created)
    fun getPlayerScreenState(): LiveData<PlayerState> = playerScreenState


    fun preparePlayer() {
        playerInteractor.preparePlayer(
            track.value!!,
            callbackPrepare = object : PlayerInteractor.PlayerConsumerPrepare {
                override fun consumePrepare() {
                    playerScreenState.value = PlayerState.Prepared
                }
            },
        )
    }

    fun playControl() {
        playerInteractor.playControl(
            callback = object : PlayerInteractor.PlayerConsumer {
                override fun consumeStart() {
                    playerScreenState.value = PlayerState.Playing(getActualTime())
                }

                override fun consumePause() {
                    playerScreenState.value = PlayerState.Paused(getActualTime())
                }

                override fun consumeControl() {
                    playerScreenState.value = PlayerState.Playing(getActualTime())
                }
            }
        )
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer(callbackPause = object : PlayerInteractor.PlayerPause {
            override fun consumePause() {
                playerScreenState.value = PlayerState.Paused(getActualTime())
            }
        })
    }

    fun releasePlayer() {
        playerInteractor.releasePlayer()
    }

    private fun getActualTime(): CharSequence {
        return playerInteractor.getActualTime()
    }

    companion object {
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val playerInteractor = Creator.getPlayerInteractor()
                val tracksIntentInteractor = Creator.getTracksIntentInteractor()

                PlayerViewModel(playerInteractor, tracksIntentInteractor)
            }
        }
    }
}