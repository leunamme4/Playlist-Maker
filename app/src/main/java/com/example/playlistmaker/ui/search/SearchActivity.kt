package com.example.playlistmaker.ui.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.TRACK_INTENT
import com.example.playlistmaker.domain.api.SearchUtilsInteractor
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.ui.player.PlayerActivity

class SearchActivity : AppCompatActivity() {

    val handler = Handler(Looper.getMainLooper())

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

    private lateinit var tracksInteractor: TracksInteractor
    private var searchUtilsInteractor = Creator.getSearchUtilsInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        tracksInteractor = Creator.getTracksInteractor()

        historyAdapter = HistoryAdapter(getHistory()) { track ->
            updateHistory(track)
        }

        trackListAdapter = TracksAdapter(getTracks()) { track ->
            updateHistory(track)
        }

        searchEditText = findViewById(R.id.search_edit)
        clearButton = findViewById(R.id.clear_button)
        backButton = findViewById(R.id.back_button)
        tracksRecyclerView = findViewById(R.id.recycler_tracks)
        noResultsPlaceholder = findViewById(R.id.no_results)
        noConnectionPlaceholder = findViewById(R.id.no_connection)
        renewButton = findViewById(R.id.renew_button)
        historyLayout = findViewById(R.id.history)
        historyRecyclerView = findViewById(R.id.history_recycler_view)
        historyClearButton = findViewById(R.id.clear_history_button)
        progressBar = findViewById(R.id.progressBar)

        // tracks RV
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = trackListAdapter

        // history RV
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
        historyRecyclerView.adapter = historyAdapter

        if (savedInstanceState != null) {
            searchEditText.setText(searchUtilsInteractor.getEditTextValue())
        }

        //backButton
        val backClickListener: View.OnClickListener = View.OnClickListener {
            this.finish()
        }
        backButton.setOnClickListener(backClickListener)

        // clear
        clearButton.setOnClickListener {
            clearEditText()
        }

        // clear history
        historyClearButton.setOnClickListener {
            clearHistory()
        }

        // repeat the last search
        renewButton.setOnClickListener {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            search(searchUtilsInteractor.getLastSearch())
        }

        // focusListener for history
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            historyLayout.visibility =
                if (hasFocus && searchEditText.text.isEmpty() && getHistory()
                        .isNotEmpty()
                ) View.VISIBLE else View.GONE
            if (historyLayout.visibility == View.VISIBLE) {
                updateVisibility(tracksRecyclerView.visibility, View.GONE, View.GONE)
            }
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(searchRunnable)
                clearButton.visibility = clearButtonVisibility(s)
                searchUtilsInteractor.setEditTextValue(s.toString())
                historyLayout.visibility =
                    if (searchEditText.hasFocus() && s.isNullOrEmpty() && getHistory()
                            .isNotEmpty()
                    ) View.VISIBLE else View.GONE
                if (historyLayout.visibility == View.VISIBLE) {
                    updateVisibility(tracksRecyclerView.visibility, View.GONE, View.GONE)
                }
                if (s.isNullOrEmpty()) {
                    updateVisibility(View.GONE, View.GONE, View.GONE)
                } else {
                    handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchUtilsInteractor.getEditTextValue())
        outState.putString(LAST_SEARCH, searchUtilsInteractor.getLastSearch())
        outState.putParcelableArrayList(TRACK_LIST, getTracks())
        outState.putInt(NO_RESULTS_VISIBLE, noResultsPlaceholder.visibility)
        outState.putInt(NO_CONNECTION_VISIBLE, noConnectionPlaceholder.visibility)
        outState.putInt(TRACKS_RECYCLER_VIEW_VISIBLE, tracksRecyclerView.visibility)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchUtilsInteractor.setEditTextValue(savedInstanceState.getString(SEARCH_TEXT, TEXT_DEFAULT))
        searchUtilsInteractor.setLastSearch(savedInstanceState.getString(LAST_SEARCH, TEXT_DEFAULT))
        val restoredTrackList = savedInstanceState.getParcelableArrayList<Track>(TRACK_LIST)
        if (restoredTrackList != null) {
            tracksInteractor.clearTracks()
            tracksInteractor.addAllTracks(restoredTrackList)
        }
        noResultsPlaceholder.visibility = savedInstanceState.getInt(NO_RESULTS_VISIBLE)
        noConnectionPlaceholder.visibility = savedInstanceState.getInt(NO_CONNECTION_VISIBLE)
        tracksRecyclerView.visibility = savedInstanceState.getInt(TRACKS_RECYCLER_VIEW_VISIBLE)
    }

    private fun updateVisibility(
        recyclerVisibility: Int,
        noConnectionVisibility: Int,
        noResultsVisibility: Int
    ) {
        tracksInteractor.clearTracks()
        trackListAdapter.notifyDataSetChanged()
        tracksRecyclerView.visibility = recyclerVisibility
        noConnectionPlaceholder.visibility = noConnectionVisibility
        noResultsPlaceholder.visibility = noResultsVisibility
    }

    private fun search(text: String) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        // сбрасываем видимость и отображаем загрузку
        updateVisibility(View.GONE, View.GONE, View.GONE)
        progressBar.visibility = View.VISIBLE

        tracksInteractor.searchTracks(text, object : TracksInteractor.TracksConsumer {
            override fun consume(foundTracks: List<Track>, status: Boolean) {
                if (!status) {
                    handler.post {
                        updateVisibility(View.GONE, View.VISIBLE, View.GONE)
                        progressBar.visibility = View.GONE
                    }
                } else if (foundTracks.isNotEmpty()) {
                    tracksInteractor.addAllTracks(foundTracks)
                    handler.post {
                        trackListAdapter.notifyDataSetChanged()
                        progressBar.visibility = View.GONE
                        tracksRecyclerView.visibility = View.VISIBLE
                        noConnectionPlaceholder.visibility = View.GONE
                        noResultsPlaceholder.visibility = View.GONE
                    }
                } else {
                    handler.post {
                        updateVisibility(View.GONE, View.GONE, View.VISIBLE)
                    }
                }
                handler.post {
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    private fun getHistory() : List<Track> {
        return tracksInteractor.getHistory()
    }

    private fun getTracks() : ArrayList<Track> {
        return tracksInteractor.getTracks()
    }

    private fun clearHistory() {
        tracksInteractor.clearHistory(object : TracksInteractor.TracksHistoryConsumer {
            override fun consume() {
                handler.post {
                    historyAdapter.notifyDataSetChanged()
                    historyLayout.visibility = View.GONE
                }
            }
        }
        )
    }

    private fun updateHistory(track: Track) {
        if (clickDebounce()) {
            tracksInteractor.updateHistory(
                track,
                object : TracksInteractor.TracksHistoryConsumer {
                    override fun consume() {
                        handler.post {
                            historyAdapter.tracks = tracksInteractor.getHistory()
                            historyAdapter.notifyDataSetChanged()
                            val navigationIntent =
                                Intent(this@SearchActivity, PlayerActivity::class.java)
                            navigationIntent.putExtra(TRACK_INTENT, track)
                            startActivity(navigationIntent)
                        }
                    }
                })
        }
    }

    private fun clearEditText() {
        searchEditText.setText(TEXT_DEFAULT)
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        updateVisibility(View.GONE, View.GONE, View.GONE)
    }

    private fun clickDebounce() : Boolean {
        return searchUtilsInteractor.clickDebounce()
    }

    val searchRunnable = searchUtilsInteractor.getSearchRunnable(object : SearchUtilsInteractor. SearchRunnableConsume {
        override fun consume() {
            search(searchUtilsInteractor.getEditTextValue())
            searchEditText.clearFocus()
        }

    })

    companion object {
        const val TRACK_LIST = "TRACK_LIST"
        const val LAST_SEARCH = "LAST_SEARCH"
        const val NO_RESULTS_VISIBLE = "NO_RESULTS_VISIBLE"
        const val NO_CONNECTION_VISIBLE = "NO_CONNECTION_VISIBLE"
        const val TRACKS_RECYCLER_VIEW_VISIBLE = "TRACKS_RECYCLER_VIEW_VISIBLE"
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT_DEFAULT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}