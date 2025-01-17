package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.search.domain.models.Track

class SearchActivity : AppCompatActivity() {

    private lateinit var trackListAdapter: TracksAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var noResultsPlaceholder: ScrollView
    private lateinit var noConnectionPlaceholder: ScrollView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var renewButton: Button
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var backButton: ImageButton
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var historyLayout: LinearLayout
    private lateinit var historyClearButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var binding: ActivitySearchBinding

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]

        historyAdapter = HistoryAdapter(getHistory()) { track ->
            viewModel.updateHistory(track)
            historyAdapterUpgrade()
            trackIntent(track)
        }

        trackListAdapter = TracksAdapter(getTrackList()) { track ->
            viewModel.updateHistory(track)
            historyAdapterUpgrade()
            trackIntent(track)
        }

        searchEditText = binding.searchEdit
        clearButton = binding.clearButton
        backButton = binding.backButton
        tracksRecyclerView = binding.recyclerTracks
        noResultsPlaceholder = binding.noResults
        noConnectionPlaceholder = binding.noConnection
        renewButton = binding.renewButton
        historyLayout = binding.history
        historyRecyclerView = binding.historyRecyclerView
        historyClearButton = binding.clearHistoryButton
        progressBar = binding.progressBar

        // tracks RV
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = trackListAdapter

        // history RV
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter


        viewModel.getSearchState().observe(this) { screenState ->
            when (screenState) {
                is SearchState.Default -> {
                    renderScreenState(View.GONE, View.GONE, View.GONE, View.GONE, View.GONE)
                }

                is SearchState.Loading -> {
                    hideKeyboard()
                    renderScreenState(View.GONE, View.GONE, View.GONE, View.GONE, View.VISIBLE)
                }

                is SearchState.Content -> {
                    trackListAdapter.notifyDataSetChanged()
                    renderScreenState(View.VISIBLE, View.GONE, View.GONE, View.GONE, View.GONE)
                }

                is SearchState.Empty -> {
                    renderScreenState(View.GONE, View.GONE, View.GONE, View.VISIBLE, View.GONE)
                }

                is SearchState.NoConnection -> {
                    renderScreenState(View.GONE, View.GONE, View.VISIBLE, View.GONE, View.GONE)
                }

                is SearchState.History -> {
                    historyAdapterUpgrade()
                    renderScreenState(View.GONE, View.VISIBLE, View.GONE, View.GONE, View.GONE)
                }
            }

        }

        //backButton
        backButton.setOnClickListener { this.finish() }

        // clear
        clearButton.setOnClickListener { clearEditText() }

        // clear history
        historyClearButton.setOnClickListener {
            viewModel.clearHistory()
            historyAdapterUpgrade()
            viewModel.setState(SearchState.Default)
        }

        // repeat the last search
        renewButton.setOnClickListener {
            hideKeyboard()
            viewModel.search(viewModel.getLastSearch())
        }

        // focusListener for history
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            historyLayout.visibility =
                if (hasFocus && searchEditText.text.isEmpty() && getHistory()
                        .isNotEmpty()
                ) View.VISIBLE else View.GONE
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.removeCallbacks()
                clearButton.visibility = clearButtonVisibility(s)
                val lastValue = getEditTextValue()
                setEditTextValue(s.toString()) // 1
                if (searchEditText.hasFocus() && s.isNullOrEmpty() && getHistory()
                        .isNotEmpty()
                ) viewModel.setState(SearchState.History) else historyLayout.visibility =
                    View.GONE
                if (searchEditText.text.toString() != lastValue && !s.isNullOrEmpty()) {
                    viewModel.runnableTask()
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        searchEditText.addTextChangedListener(searchTextWatcher)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
    }

    private fun getHistory(): List<Track> {
        return viewModel.history
    }

    private fun getTrackList(): ArrayList<Track> {
        return viewModel.tracks
    }

    private fun trackIntent(track: Track) {
        hideKeyboard()
        val navigationIntent =
            Intent(this@SearchActivity, PlayerActivity::class.java)
        viewModel.trackTransition(track)
        startActivity(navigationIntent)
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

    fun getEditTextValue(): String {
        return viewModel.getEditTextValue()
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

    private fun historyAdapterUpgrade() {
        historyAdapter.tracks = getHistory()
        historyAdapter.notifyDataSetChanged()
    }

    companion object {
        const val TEXT_DEFAULT = ""
    }
}