package com.example.playlistmaker.media.ui.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.root.RootActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    companion object {
        fun newInstance() = PlaylistsFragment()
    }

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<PlaylistsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistButton.setOnClickListener {
            (activity as? RootActivity)?.hideNavigationBar()
            findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
        }

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 2) //ориентация по умолчанию — вертикальная

        viewModel.getPlaylistsState().observe(viewLifecycleOwner) { state ->
            renderState(state)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylists()
    }

    private fun renderState(state: PlaylistsState) {
        when(state) {
            is PlaylistsState.Empty -> showEmpty()
            is PlaylistsState.Content -> showContent(state.tracks)
        }
    }

    private fun showEmpty(){
        binding.recyclerView.visibility = View.GONE
        binding.noContent.visibility = View.VISIBLE
    }

    private fun showContent(playlists: List<Playlist>) {
        binding.recyclerView.adapter = PlaylistsAdapter(playlists) { playlist ->
            val bundle = Bundle().apply {
                putInt("id", playlist.id)
            }
            findNavController().navigate(R.id.action_mediaFragment_to_playlistFragment, bundle)
        }
        binding.recyclerView.visibility = View.VISIBLE
        binding.noContent.visibility = View.GONE
    }
}