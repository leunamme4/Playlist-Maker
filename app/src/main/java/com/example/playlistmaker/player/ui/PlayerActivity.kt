package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.creator.Creator.getPlayerInteractor
import com.example.playlistmaker.R
import com.example.playlistmaker.TRACK_INTENT
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.search.domain.models.Track

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

    private lateinit var binding: ActivityPlayerBinding

    private val playerInteractor = getPlayerInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        track = intent.getParcelableExtra(TRACK_INTENT)!!
        backButton = binding.backButtonPlayer
        nameData = binding.name
        artistData = binding.artist
        timeData = binding.timeData
        actualTime = binding.actualTime
        albumData = binding.albumData
        yearData = binding.yearData
        genreData = binding.genreData
        countryData = binding.countryData
        cover = binding.cover
        albumGroup = binding.albumGroup
        playButton = binding.playButton

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
        releasePlayer()
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
        playerInteractor.preparePlayer(
            track,
            callbackPrepare = object : PlayerInteractor.PlayerConsumerPrepare {
                override fun consumePrepare() {
                    actualTime.text = getString(R.string.track_play_start)
                    loadPlayButton(R.drawable.play)
                }
            },
        )
    }

    private fun pausePlayer() {
        playerInteractor.pausePlayer(callbackPause = object : PlayerInteractor.PlayerPause {
            override fun consumePause() {
                loadPlayButton(R.drawable.play)
            }
        })
    }

    private fun playControl() {
        playerInteractor.playControl(
            callback = object : PlayerInteractor.PlayerConsumer {
                override fun consumeStart() {
                    loadPlayButton(R.drawable.pause)
                }

                override fun consumePause() {
                    loadPlayButton(R.drawable.play)
                }

                override fun consumeControl() {
                    actualTime.text = getActualTime()
                }
            }
        )
    }

    private fun releasePlayer() {
        playerInteractor.releasePlayer()
    }

    private fun loadPlayButton(resourceId: Int) {
        Glide.with(playButton)
            .load(resourceId)
            .into(playButton)
    }

    private fun getActualTime() : CharSequence {
        return playerInteractor.getActualTime()
    }
}