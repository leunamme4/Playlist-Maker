package com.example.playlistmaker.player.ui

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.FavoritesInteractor
import com.example.playlistmaker.search.domain.api.TracksIntentInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.getKoin
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    tracksIntentInteractor: TracksIntentInteractor,
    private var mediaPlayer: MediaPlayer,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    init {
        viewModelScope.launch {
            val trackAsync = async { tracksIntentInteractor.getPlayerTrack() }
            val favorite = trackAsync.await().isFavorite
            track.postValue(trackAsync.await())
            isFavorite.postValue(favorite)
        }
    }

    private var track = MutableLiveData<Track>()

    fun observeTrack(): LiveData<Track> = track

    private val isFavorite: MutableLiveData<Boolean> = MutableLiveData<Boolean>()
    fun observeFavorite(): LiveData<Boolean> = isFavorite

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
        when (playerScreenState.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }

            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
            }

            else -> {}
        }
    }

    fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    fun setPlayerState(state: PlayerState) {
        playerScreenState.postValue(state)
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

    fun onFavoriteClick() {
        viewModelScope.launch {
            if (!isFavorite.value!!) {
                favoritesInteractor.addToFavorites(track.value!!.also {
                    it.isFavorite = !it.isFavorite
                })
            } else favoritesInteractor.deleteFromFavorites(track.value!!)
            isFavorite.postValue(!isFavorite.value!!)
        }
    }
}