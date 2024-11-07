package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.log

class SearchActivity : AppCompatActivity() {
    var editTextValue = TEXT_DEFAULT
    private var lastSearch = ""
    val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksApiService = retrofit.create(TracksApiService::class.java)
    private val trackList = ArrayList<Track>()
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
    private lateinit var searchHistory: SearchHistory
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPreferences = getSharedPreferences(HISTORY_PREFERENCES, MODE_PRIVATE)
        searchHistory = SearchHistory(sharedPreferences)
        historyAdapter = HistoryAdapter(searchHistory.historyArrayList) { track ->
            if (clickDebounce()) {
                searchHistory.updateHistory(track)
                historyAdapter.notifyDataSetChanged()
                val navigationIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                navigationIntent.putExtra(TRACK_INTENT, track)
                startActivity(navigationIntent)
            }
        }
        trackListAdapter = TracksAdapter(trackList) { track ->
            if (clickDebounce()) {
                searchHistory.updateHistory(track)
                historyAdapter.notifyDataSetChanged()
                val navigationIntent = Intent(this@SearchActivity, PlayerActivity::class.java)
                navigationIntent.putExtra(TRACK_INTENT, track)
                startActivity(navigationIntent)
            }
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
            searchEditText.setText(editTextValue)
        }

        //backButton
        val backClickListener: View.OnClickListener = View.OnClickListener {
            this.finish()
        }
        backButton.setOnClickListener(backClickListener)

        // clear
        clearButton.setOnClickListener {
            searchEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            updateVisibility(View.GONE, View.GONE, View.GONE)
        }

        // clear history
        historyClearButton.setOnClickListener {
            searchHistory.clearHistory()
            historyAdapter.notifyDataSetChanged()
            historyLayout.visibility = View.GONE
        }

        // repeat the last search
        renewButton.setOnClickListener {
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            search(lastSearch)
        }

        // focusListener for history
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            historyLayout.visibility =
                if (hasFocus && searchEditText.text.isEmpty() && searchHistory.historyArrayList.isNotEmpty()) View.VISIBLE else View.GONE
            if (historyLayout.visibility == View.VISIBLE) {
                updateVisibility(tracksRecyclerView.visibility, View.GONE, View.GONE)
            }
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacks(searchRunnable)
                clearButton.visibility = clearButtonVisibility(s)
                editTextValue = s.toString()
                historyLayout.visibility =
                    if (searchEditText.hasFocus() && s.isNullOrEmpty() && searchHistory.historyArrayList.isNotEmpty()) View.VISIBLE else View.GONE
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
        outState.putString(SEARCH_TEXT, editTextValue)
        outState.putString("LAST_SEARCH", lastSearch)
        outState.putParcelableArrayList("TRACK_LIST", trackList)
        outState.putInt("NO_RESULTS_VISIBLE", noResultsPlaceholder.visibility)
        outState.putInt("NO_CONNECTION_VISIBLE", noConnectionPlaceholder.visibility)
        outState.putInt("TRACKS_RECYCLER_VIEW_VISIBLE", tracksRecyclerView.visibility)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editTextValue = savedInstanceState.getString(SEARCH_TEXT, TEXT_DEFAULT)
        lastSearch = savedInstanceState.getString("LAST_SEARCH", "")
        val restoredTrackList = savedInstanceState.getParcelableArrayList<Track>("TRACK_LIST")
        if (restoredTrackList != null) {
            trackList.clear()
            trackList.addAll(restoredTrackList)
        }
        noResultsPlaceholder.visibility = savedInstanceState.getInt("NO_RESULTS_VISIBLE")
        noConnectionPlaceholder.visibility = savedInstanceState.getInt("NO_CONNECTION_VISIBLE")
        tracksRecyclerView.visibility = savedInstanceState.getInt("TRACKS_RECYCLER_VIEW_VISIBLE")
    }

    private fun updateVisibility(
        recyclerVisibility: Int,
        noConnectionVisibility: Int,
        noResultsVisibility: Int
    ) {
        trackList.clear()
        trackListAdapter.notifyDataSetChanged()
        tracksRecyclerView.visibility = recyclerVisibility
        noConnectionPlaceholder.visibility = noConnectionVisibility
        noResultsPlaceholder.visibility = noResultsVisibility
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun search(text: String) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        // сбрасываем видимость и отображаем загрузку
        updateVisibility(View.GONE, View.GONE, View.GONE)
        progressBar.visibility = View.VISIBLE

        tracksApiService.search(text).enqueue(object : Callback<TrackList> {
            override fun onResponse(
                call: Call<TrackList>,
                response: Response<TrackList>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {
                        if (response.body()!!.results.isNotEmpty()) {
                            val tracks = response.body()!!.results
                            trackList.clear()
                            tracks.map {
                                it.trackTimeMillis = SimpleDateFormat(
                                    "mm:ss",
                                    Locale.getDefault()
                                ).format(it.trackTimeMillis.toLongOrNull())
                            }
                            trackList.addAll(tracks)
                            trackListAdapter.notifyDataSetChanged()
                            tracksRecyclerView.visibility = View.VISIBLE
                            noConnectionPlaceholder.visibility = View.GONE
                            noResultsPlaceholder.visibility = View.GONE
                        } else {
                            updateVisibility(View.GONE, View.GONE, View.VISIBLE)
                        }
                    } else {
                        updateVisibility(View.GONE, View.GONE, View.VISIBLE)
                    }
                } else {
                    updateVisibility(View.GONE, View.VISIBLE, View.GONE)
                }
                progressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<TrackList>, t: Throwable) {
                trackList.clear()
                trackListAdapter.notifyDataSetChanged()
                if (noResultsPlaceholder.visibility == View.VISIBLE) {
                    noResultsPlaceholder.visibility = View.GONE
                }
                noConnectionPlaceholder.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        })
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val TEXT_DEFAULT = ""
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    val searchRunnable = Runnable {
        lastSearch = editTextValue
        search(editTextValue)
        searchEditText.clearFocus()
    }

}