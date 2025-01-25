package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import android.os.Handler
import android.util.Log
import com.example.playlistmaker.search.domain.models.Track
import org.koin.java.KoinJavaComponent.getKoin
import java.text.SimpleDateFormat
import java.util.Locale

class Player (private val handler: Handler, private var playTimeRunnable: Runnable, private var mediaPlayer: MediaPlayer) {

    private var playerState = STATE_DEFAULT

    fun preparePlayer(track: Track, callback: () -> Unit) {
        mediaPlayer = getKoin().get()
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            handler.removeCallbacks(playTimeRunnable)
            callback()
        }
    }

    private fun startPlayer(callback: () -> Unit, callbackStart: () -> Unit) {
        mediaPlayer.start()
        callbackStart()
        playTimeTask(callback)
        playerState = STATE_PLAYING
        handler.post(playTimeRunnable)
    }

    fun pausePlayer(callbackPause: () -> Unit) {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        callbackPause()
        handler.removeCallbacks(playTimeRunnable)
    }

    fun playControl(callback: () -> Unit, callbackStart: () -> Unit, callbackPause: () -> Unit) {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer(callbackPause)
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer(callback, callbackStart)
            }
        }
    }

    fun releasePlayer() {
        mediaPlayer.release()
    }

    private fun playTimeTask(callback: () -> Unit) {
        playTimeRunnable = object : Runnable {
            override fun run() {
                callback()
                handler.postDelayed(this, DELAY_CHECK_TIME)
            }
        }
    }

    fun actualTime(): CharSequence {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.currentPosition)
    }

    // состояния воспроизведения
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val DELAY_CHECK_TIME = 30L
    }
}