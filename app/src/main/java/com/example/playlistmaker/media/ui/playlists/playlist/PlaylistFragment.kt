package com.example.playlistmaker.media.ui.playlists.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.root.RootActivity
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TracksAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment : Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistViewModel>()

    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var trackListAdapter: TracksAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetBehavior_edit: BottomSheetBehavior<LinearLayout>
    private lateinit var bundle: Bundle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? RootActivity)?.hideNavigationBar()
        val id = arguments?.getInt("id")

        viewModel.getPlaylistInfo(id!!)

        tracksRecyclerView = binding.tracksRv

        viewModel.playlistInfo.observe(viewLifecycleOwner) { playlist ->
            binding.name.text = playlist.name
            binding.description.text = playlist.description
            binding.tracksCount.text = "${playlist.tracksCount} ${trackCountFormat(playlist.tracksCount)}"
            Glide.with(binding.cover)
                .load(playlist.coverPath)
                .placeholder(R.drawable.track_cover_placeholder)
                .into(binding.cover)

            bundle = Bundle().apply {
                putInt("id", playlist.id)
            }
        }

        viewModel.generalTime.observe(viewLifecycleOwner) { time ->
            binding.playlistTime.text = time
        }

        viewModel.tracksState.observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

        viewModel.deleted.observe(viewLifecycleOwner) { deleted ->
            if (deleted) findNavController().popBackStack()
        }

        viewModel.toast.observe(viewLifecycleOwner) {
            Toast.makeText(activity, "Плейлист пуст", Toast.LENGTH_LONG)
                .show()
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSv).apply {
            isHideable = false
            isDraggable = true
            state = BottomSheetBehavior.STATE_EXPANDED
        }

        bottomSheetBehavior_edit = BottomSheetBehavior.from(binding.bottomSvEdit).apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior_edit.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            val overlay = binding.overlay
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        playlistBsInflate()
                        overlay.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }

                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.shareButton.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.editButton.setOnClickListener {
            bottomSheetBehavior_edit.state = BottomSheetBehavior.STATE_EXPANDED
        }

        binding.shareBs.setOnClickListener {
            viewModel.sharePlaylist()
        }

        binding.deleteBs.setOnClickListener {
            dialogPlaylist()
        }

        binding.editBs.setOnClickListener {
            // переход на фрагмент редактирования плейлиста
            findNavController().navigate(R.id.action_playlistFragment_to_createPlaylistFragment, bundle)
        }

    }

    override fun onResume() {
        super.onResume()
        val id = arguments?.getInt("id")
        viewModel.getPlaylistInfo(id!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? RootActivity)?.showNavigationBar()
    }

    private fun renderState(state: TracksState) {
        when (state) {
            is TracksState.Content -> rvInit(state.tracks)
            TracksState.Empty -> emptyState()
        }
    }


    fun playlistBsInflate() {
        binding.nameBs.text = binding.name.text
        binding.tracksCountBs.text = binding.tracksCount.text
        Glide.with(binding.coverBs)
            .load(binding.cover.drawable)
            .placeholder(R.drawable.track_cover_placeholder)
            .into(binding.coverBs)
    }

    private fun trackCountFormat(count: Int): String {
        return when(count % 10) {
            0, in 5..9 -> "треков"
            1 -> "трек"
            in 2..4 -> "трека"
            else -> {""}
        }
    }

    private fun trackIntent(track: Track) {
        viewModel.trackTransition(track)
        findNavController().navigate(R.id.action_playlistFragment_to_playerActivity)
    }

    // tracks RV
    private fun rvInit(tracks: List<Track>) {
        trackListAdapter = TracksAdapter(
            tracks,
            listener = { track -> trackIntent(track) },
            longClick = { track -> dialogTrack(track) }
        )
        trackListAdapter.notifyDataSetChanged()
        tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        tracksRecyclerView.adapter = trackListAdapter
        binding.noContent.visibility = View.GONE
        binding.noContentText.visibility = View.GONE
        tracksRecyclerView.visibility = View.VISIBLE
    }

    private fun emptyState() {
        tracksRecyclerView.visibility = View.GONE
        binding.noContent.visibility = View.VISIBLE
        binding.noContentText.visibility = View.VISIBLE
    }

    private fun dialogTrack(track: Track) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить трек")
            .setMessage("Вы уверены, что хотите удалить трек из плейлиста?")
            .setNegativeButton("Отмена") { dialog, which ->

            }.setPositiveButton("Удалить") { dialog, which ->
                viewModel.deleteTrack(track)
                trackListAdapter.notifyDataSetChanged()
            }.show()
    }

    private fun dialogPlaylist() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Удалить плейлист")
            .setMessage("Вы уверены, что хотите удалить этот плейлист?")
            .setNegativeButton("Отмена") { dialog, which ->

            }.setPositiveButton("Удалить") { dialog, which ->
                viewModel.deletePlaylist()
            }.show()
    }


}