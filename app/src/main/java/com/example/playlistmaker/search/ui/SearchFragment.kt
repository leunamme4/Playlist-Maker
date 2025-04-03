package com.example.playlistmaker.search.ui

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private lateinit var trackListAdapter: TracksAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var noResultsPlaceholder: ScrollView
    private lateinit var noConnectionPlaceholder: ScrollView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var renewButton: Button
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyClearButton: Button
    private lateinit var progressBar: ProgressBar

    private val viewModel by viewModel<SearchViewModel>()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        historyAdapter = HistoryAdapter(emptyList()) { track ->
            viewModel.updateHistory(track)
            viewModel.setHistoryState()
            trackIntent(track)
        }

        trackListAdapter = TracksAdapter(emptyList(), listener = { track ->
            viewModel.updateHistory(track)
            trackIntent(track)
        })

        searchEditText = binding.searchEdit
        clearButton = binding.clearButton
        tracksRecyclerView = binding.recyclerTracks
        noResultsPlaceholder = binding.noResults
        noConnectionPlaceholder = binding.noConnection
        renewButton = binding.renewButton
        historyLayout = binding.history
        historyRecyclerView = binding.historyRecyclerView
        historyClearButton = binding.clearHistoryButton
        progressBar = binding.progressBar
        var newLastValue = TEXT_DEFAULT

        // tracks RV
        tracksRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        tracksRecyclerView.adapter = trackListAdapter

        // history RV
        historyRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        historyRecyclerView.adapter = historyAdapter

        viewModel.getSearchState().observe(viewLifecycleOwner) { screenState ->
            when (screenState) {
                is SearchState.Default -> {
                    renderScreenState(
                        tracksVisibility = View.GONE,
                        historyVisibility = View.GONE,
                        noConnectionVisibility = View.GONE,
                        noResultsVisibility = View.GONE,
                        progressBarVisibility = View.GONE
                    )
                }

                is SearchState.Loading -> {
                    hideKeyboard()
                    renderScreenState(
                        tracksVisibility = View.GONE,
                        historyVisibility = View.GONE,
                        noConnectionVisibility = View.GONE,
                        noResultsVisibility = View.GONE,
                        progressBarVisibility = View.VISIBLE
                    )
                }

                is SearchState.Content -> {
                    trackListAdapter.tracks = screenState.tracks
                    trackListAdapter.notifyDataSetChanged()
                    renderScreenState(
                        tracksVisibility = View.VISIBLE,
                        historyVisibility = View.GONE,
                        noConnectionVisibility = View.GONE,
                        noResultsVisibility = View.GONE,
                        progressBarVisibility = View.GONE
                    )
                }

                is SearchState.Empty -> {
                    renderScreenState(
                        tracksVisibility = View.GONE,
                        historyVisibility = View.GONE,
                        noConnectionVisibility = View.GONE,
                        noResultsVisibility = View.VISIBLE,
                        progressBarVisibility = View.GONE
                    )
                }

                is SearchState.NoConnection -> {
                    renderScreenState(
                        tracksVisibility = View.GONE,
                        historyVisibility = View.GONE,
                        noConnectionVisibility = View.VISIBLE,
                        noResultsVisibility = View.GONE,
                        progressBarVisibility = View.GONE
                    )
                }

                is SearchState.History -> {
                    historyAdapter.tracks = screenState.tracks
                    historyAdapter.notifyDataSetChanged()
                    val historyVisibility =
                        if (screenState.tracks.isNotEmpty()) View.VISIBLE else View.GONE
                    renderScreenState(
                        tracksVisibility = View.GONE,
                        historyVisibility = historyVisibility,
                        noConnectionVisibility = View.GONE,
                        noResultsVisibility = View.GONE,
                        progressBarVisibility = View.GONE
                    )
                }
            }

        }

        // clear
        clearButton.setOnClickListener { clearEditText() }

        // clear history
        historyClearButton.setOnClickListener {
            viewModel.clearHistory()
            viewModel.setState(SearchState.Default)
        }

        // repeat the last search
        renewButton.setOnClickListener {
            hideKeyboard()
            viewModel.search(TEXT_DEFAULT)
        }

        // focusListener for history
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchEditText.text.isEmpty()) viewModel.setHistoryState()
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.cancelSearch()
                clearButton.isGone = s.isNullOrEmpty()
                val lastValue = newLastValue
                newLastValue = s.toString()
                setEditTextValue(newLastValue)
                if (searchEditText.hasFocus() && s.isNullOrEmpty()
                ) viewModel.setHistoryState() else historyLayout.visibility =
                    View.GONE
                if (searchEditText.text.toString() != lastValue && !s.isNullOrEmpty()) {
                    viewModel.searchTask()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        searchEditText.addTextChangedListener(searchTextWatcher)
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun trackIntent(track: Track) {
        hideKeyboard()
        viewModel.trackTransition(track)
        findNavController().navigate(R.id.action_searchFragment_to_playerActivity)
    }

    private fun clearEditText() {
        searchEditText.setText(TEXT_DEFAULT)
        hideKeyboard()
        searchEditText.clearFocus()
        viewModel.setState(SearchState.Default)
    }

    fun setEditTextValue(text: String) {
        viewModel.setEditTextValue(text)
    }

    private fun renderScreenState(
        tracksVisibility: Int,
        historyVisibility: Int,
        noConnectionVisibility: Int,
        noResultsVisibility: Int,
        progressBarVisibility: Int
    ) {
        tracksRecyclerView.visibility = tracksVisibility
        historyLayout.visibility = historyVisibility
        noConnectionPlaceholder.visibility = noConnectionVisibility
        noResultsPlaceholder.visibility = noResultsVisibility
        progressBar.visibility = progressBarVisibility
    }

    private companion object {
        const val TEXT_DEFAULT = ""
    }
}