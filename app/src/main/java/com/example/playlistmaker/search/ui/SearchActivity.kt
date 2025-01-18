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
import androidx.core.view.isGone
import androidx.core.view.isVisible
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

        historyAdapter = HistoryAdapter(emptyList()) { track ->
            viewModel.updateHistory(track)
            trackIntent(track)
        }

        trackListAdapter = TracksAdapter(emptyList()) { track ->
            viewModel.updateHistory(track)
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
        var historySize = 0
        var newLastValue = TEXT_DEFAULT

        // tracks RV
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = trackListAdapter

        // history RV
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        viewModel.observeTracks().observe(this) {
            trackListAdapter.tracks = it
        }

        viewModel.observeHistory().observe(this) {
            historySize = it.size
            historyAdapter.tracks = it
            historyAdapter.notifyDataSetChanged()
        }

        viewModel.getSearchState().observe(this) { screenState ->
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
                    renderScreenState(
                        tracksVisibility = View.GONE,
                        historyVisibility = View.VISIBLE,
                        noConnectionVisibility = View.GONE,
                        noResultsVisibility = View.GONE,
                        progressBarVisibility = View.GONE
                    )
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
            viewModel.setState(SearchState.Default)
        }

        // repeat the last search
        renewButton.setOnClickListener {
            hideKeyboard()
            viewModel.search(TEXT_DEFAULT)
        }

        // focusListener for history
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            historyLayout.isVisible =
                hasFocus && searchEditText.text.isEmpty() && historySize > 0
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.removeCallbacks()
                clearButton.isGone = s.isNullOrEmpty()
                val lastValue = newLastValue
                newLastValue = s.toString()
                setEditTextValue(newLastValue)
                if (searchEditText.hasFocus() && s.isNullOrEmpty() && historySize > 0
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

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
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