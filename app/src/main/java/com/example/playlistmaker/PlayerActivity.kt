package com.example.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        track = intent.getParcelableExtra("track")!!

        backButton = findViewById(R.id.back_button)
        nameData = findViewById(R.id.name)
        artistData = findViewById(R.id.artist)
        timeData = findViewById(R.id.time_data)
        albumData = findViewById(R.id.album_data)
        yearData = findViewById(R.id.year_data)
        genreData = findViewById(R.id.genre_data)
        countryData = findViewById(R.id.country_data)
        cover = findViewById(R.id.cover)
        albumGroup = findViewById(R.id.album_group)


        //backButton
        val backClickListener: View.OnClickListener = View.OnClickListener {
            this.finish()
        }
        backButton.setOnClickListener(backClickListener)

        trackInflate()
    }
}