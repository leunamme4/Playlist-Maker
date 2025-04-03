package com.example.playlistmaker.media.ui.favorites

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoritesBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TracksAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoritesFragment : Fragment() {

    private lateinit var trackListAdapter: TracksAdapter
    private lateinit var tracksRecyclerView: RecyclerView

    companion object {
        fun newInstance() = FavoritesFragment()
    }

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<FavoritesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tracksRecyclerView = binding.rvFavorites

        viewModel.getFavoritesState().observe(viewLifecycleOwner) { screenState ->
            renderState(screenState)
        }

    }

    override fun onResume() {
        super.onResume()
        viewModel.getFavorites()
    }

    private fun trackIntent(track: Track) {
        viewModel.trackTransition(track)
        findNavController().navigate(R.id.action_mediaFragment_to_playerActivity)
    }

    private fun renderState(state: FavoritesState) {
        when(state) {
            is FavoritesState.Content -> rvInit(state.tracks)
            FavoritesState.Empty -> emptyState()
        }
    }

    // tracks RV
    private fun rvInit(tracks: List<Track>) {
        trackListAdapter = TracksAdapter(tracks) { track ->
            trackIntent(track)
        }
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

}