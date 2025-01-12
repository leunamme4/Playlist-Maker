package com.example.playlistmaker

import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private lateinit var track: Track
    private lateinit var backButton: ImageButton
    private lateinit var nameData: TextView
    private lateinit var artistData: TextView
    private lateinit var timeData: TextView
    private lateinit var albumData: TextView
    private lateinit var yearData: TextView
    private lateinit var genreData: TextView
    private lateinit var countryData: TextView
    private lateinit var cover: ImageView
    private lateinit var albumGroup: Group
    private lateinit var playButton: ImageButton
    private lateinit var actualTime: TextView
    private var playTimeRunnable: Runnable = Runnable{}

    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        track = intent.getParcelableExtra(TRACK_INTENT)!!
        backButton = findViewById(R.id.back_button_player)
        nameData = findViewById(R.id.name)
        artistData = findViewById(R.id.artist)
        timeData = findViewById(R.id.time_data)
        actualTime = findViewById(R.id.actual_time)
        albumData = findViewById(R.id.album_data)
        yearData = findViewById(R.id.year_data)
        genreData = findViewById(R.id.genre_data)
        countryData = findViewById(R.id.country_data)
        cover = findViewById(R.id.cover)
        albumGroup = findViewById(R.id.album_group)
        playButton = findViewById(R.id.play_button)

        //backButton
        val backClickListener: View.OnClickListener = View.OnClickListener {
            finish()
        }
        backButton.setOnClickListener(backClickListener)

        trackInflate()

        preparePlayer(track)
        playButton.setOnClickListener {
            playControl()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun trackInflate() {
        nameData.text = track.trackName
        artistData.text = track.artistName
        timeData.text = track.trackTimeMillis
        if (track.collectionName.isEmpty())
            albumGroup.visibility = View.GONE else albumData.text = track.collectionName
        yearData.text = track.getYear()
        genreData.text = track.primaryGenreName
        countryData.text = track.country
        Glide.with(cover)
            .load(track.getCoverArtwork())
            .centerCrop()
            .placeholder(R.drawable.placeholder_player_light)
            .transform(RoundedCorners(cover.context.resources.getDimensionPixelSize(R.dimen.player_cover_corner_radius)))
            .into(cover)
    }

    private fun preparePlayer(track: Track) {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            //playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            //play.text = "PLAY"
            handler.removeCallbacks(playTimeRunnable)
            actualTime.text = getString(R.string.track_play_start)
            loadPlayButton(R.drawable.play)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        loadPlayButton(R.drawable.pause)
        playTimeTask()
        handler.post(playTimeRunnable)
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        loadPlayButton(R.drawable.play)
        handler.removeCallbacks(playTimeRunnable)
        playerState = STATE_PAUSED
    }

    private fun playControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun playTimeTask() {
        playTimeRunnable = object : Runnable {
            override fun run() {
                Log.d("my", "runnin")
                //timePassed = System.currentTimeMillis() - startTime
                actualTime.text = SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(mediaPlayer.currentPosition)

                handler.postDelayed(this, DELAY_CHECK_TIME)
            }
        }
    }

    private fun loadPlayButton(resourceId: Int) {
        Glide.with(playButton)
            .load(resourceId)
            .into(playButton)
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