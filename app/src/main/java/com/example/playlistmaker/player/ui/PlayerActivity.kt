package com.example.playlistmaker.player.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentContainerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.media.ui.playlists.create_playlist.CreatePlaylistFragment
import com.example.playlistmaker.media.ui.playlists.PlaylistsState
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {
    private lateinit var playerView: ScrollView
    private lateinit var fragmentContainer: FragmentContainerView
    private lateinit var backButton: ImageButton
    private lateinit var likeButton: ImageButton
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
    private lateinit var bottomSV: LinearLayout
    private lateinit var playlistsRv: RecyclerView
    private lateinit var addToPlaylist: ImageButton
    private lateinit var overlay: View
    private lateinit var newPlaylist: Button
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    private lateinit var binding: ActivityPlayerBinding

    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerView = binding.playerView
        fragmentContainer = binding.fragmentContainer
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
        likeButton = binding.likeButton
        bottomSV = binding.bottomSv
        playlistsRv = binding.playlistsRv
        addToPlaylist = binding.addToPlaylistButton
        overlay = binding.overlay
        newPlaylist = binding.newPlaylistButton

        playlistsRv.layoutManager = LinearLayoutManager(this)

        //backButton
        val backClickListener: View.OnClickListener = View.OnClickListener {
            finish()
        }
        backButton.setOnClickListener(backClickListener)

        viewModel.getPlaylistsState().observe(this) { state ->
            renderState(state)
        }

        fun startPlayer() {
            viewModel.getPlayerScreenState().observe(this) { screenState ->
                when (screenState) {
                    is PlayerState.Paused -> {
                        loadPlayButton(R.drawable.play)
                        actualTime.text = screenState.time
                    }

                    is PlayerState.Playing -> {
                        loadPlayButton(R.drawable.pause)
                        actualTime.text = screenState.time
                    }

                    is PlayerState.Prepared -> {
                        actualTime.text = getString(R.string.track_play_start)
                        loadPlayButton(R.drawable.play)
                    }

                    is PlayerState.Created -> {
                        viewModel.preparePlayer()
                    }

                    else -> {}
                }
            }
        }

        viewModel.observeTrack().observe(this) {
            trackInflate(it)
            startPlayer()
        }

        viewModel.observeAdded().observe(this){ state ->
            when(state) {
                is AddToPlaylistState.Added -> {
                    Toast.makeText(this, "Добавлено в плейлист ${state.playlist.name}", Toast.LENGTH_LONG)
                    .show()
                }
                is AddToPlaylistState.NotAdded -> {
                    Toast.makeText(this, "Трек уже добавлен в плейлист ${state.playlist.name}", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }

        viewModel.observeFavorite().observe(this) { isFavorite ->
            loadLikeButton(isFavorite)
        }

        playButton.setOnClickListener {
            viewModel.playControl()
        }

        likeButton.setOnClickListener {
            viewModel.onFavoriteClick()
        }

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSV).apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        overlay.visibility = View.VISIBLE
                        viewModel.getPlaylists()
                        bottomSheet.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                        bottomSheet.visibility = View.GONE
                        bottomSV.isVisible = false
                    }

                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        addToPlaylist.setOnClickListener {
            if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            } else bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

        newPlaylist.setOnClickListener {
            playerView.isVisible = false
            fragmentContainer.isVisible = true
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            supportFragmentManager.beginTransaction()
                .addToBackStack("newPlaylist")
                .add(R.id.fragment_container, CreatePlaylistFragment())
                .commit()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) viewModel.releasePlayer()
    }

    private fun trackInflate(track: Track) {
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

    private fun loadLikeButton(isFavorite: Boolean) {
        if (isFavorite) {
            likeButton.setImageResource(R.drawable.liked)
        } else likeButton.setImageResource(R.drawable.like)
    }

    private fun loadPlayButton(resourceId: Int) {
        Glide.with(playButton)
            .load(resourceId)
            .into(playButton)
    }

    private fun renderState(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showEmpty()
            is PlaylistsState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmpty() {
        playlistsRv.visibility = View.GONE
    }

    private fun showContent(playlists: List<Playlist>) {
        playlistsRv.adapter = PlaylistBsAdapter(playlists) { playlist ->
            viewModel.addTrackToPlaylist(playlist)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
        playlistsRv.visibility = View.VISIBLE
    }

    fun returnFromCreatePlaylist() {
        if (playerView != null && fragmentContainer != null) {
            playerView.isVisible = true
            fragmentContainer.isVisible = false
        }
    }
}