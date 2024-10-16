package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

class SearchActivity : AppCompatActivity() {
    var editTextValue = TEXT_DEFAULT
    private var lastSearch = ""

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val tracksApiService = retrofit.create(TracksApiService::class.java)
    private val trackList = ArrayList<Track>()
    private val trackListAdapter = TracksAdapter(trackList)
    private lateinit var noResultsPlaceholder: ScrollView
    private lateinit var noConnectionPlaceholder: ScrollView
    private lateinit var tracksRecyclerView: RecyclerView
    private lateinit var renewButton: Button
    private lateinit var searchEditText: EditText
    private lateinit var clearButton: ImageButton
    private lateinit var backButton: ImageButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchEditText = findViewById(R.id.search_edit)
        clearButton = findViewById(R.id.clear_button)
        backButton = findViewById(R.id.back_button)
        tracksRecyclerView = findViewById(R.id.recycler_tracks)
        noResultsPlaceholder = findViewById(R.id.no_results)
        noConnectionPlaceholder = findViewById(R.id.no_connection)
        renewButton = findViewById(R.id.renew_button)

        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksRecyclerView.adapter = trackListAdapter

        if (savedInstanceState != null) {
            searchEditText.setText(editTextValue)
        }


        //backButton
        val backClickListener: View.OnClickListener = View.OnClickListener {
            this.finish()
        }
        backButton.setOnClickListener(backClickListener)

        //clear
        clearButton.setOnClickListener {
            searchEditText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            updateVisibility(View.GONE, View.GONE, View.GONE)
        }

        // repeat the last search
        renewButton.setOnClickListener {
            search(lastSearch)
        }

        // search by ACTION.DONE click
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                lastSearch = editTextValue
                search(editTextValue)
                true
            }
            false
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                editTextValue = s.toString()
                if (s.isNullOrEmpty()) {
                    updateVisibility(View.GONE, View.GONE, View.GONE)
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

    private fun search(text: String) {
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
            }

            override fun onFailure(call: Call<TrackList>, t: Throwable) {
                trackList.clear()
                trackListAdapter.notifyDataSetChanged()
                if (noResultsPlaceholder.visibility == LinearLayout.VISIBLE) {
                    noResultsPlaceholder.visibility = LinearLayout.GONE
                }
                noConnectionPlaceholder.visibility = LinearLayout.VISIBLE
            }
        })
    }

    companion object {
        val SEARCH_TEXT = "SEARCH_TEXT"
        val TEXT_DEFAULT = ""
    }

}