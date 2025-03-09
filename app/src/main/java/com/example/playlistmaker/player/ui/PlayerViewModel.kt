package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.api.TracksIntentInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    tracksIntentInteractor: TracksIntentInteractor,
    private var mediaPlayer: MediaPlayer,
) : ViewModel() {

    private var track = MutableLiveData(tracksIntentInteractor.getPlayerTrack())

    fun observeTrack(): LiveData<Track> = track

    private var playerScreenState = MutableLiveData<PlayerState>(PlayerState.Created)
    fun getPlayerScreenState(): LiveData<PlayerState> = playerScreenState

    private var timerJob: Job? = null

    fun preparePlayer() {
        mediaPlayer = getKoin().get()
        mediaPlayer.setDataSource(track.value!!.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerScreenState.value = PlayerState.Prepared
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerScreenState.value = PlayerState.Prepared
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerScreenState.value = PlayerState.Playing(actualTime())
        playTimeTask()
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        playerScreenState.value = PlayerState.Paused(actualTime())
        timerJob?.cancel()
    }

    fun playControl() {
        when(playerScreenState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }
            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }
            else -> { }
        }
    }

    fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    private fun playTimeTask() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(300L)
                playerScreenState.postValue(PlayerState.Playing(actualTime()))
            }
        }
    }

    private fun actualTime(): CharSequence {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.currentPosition)
    }
}