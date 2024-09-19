package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    var editTextValue = TEXT_DEFAULT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchEditText = findViewById<EditText>(R.id.search_edit)
        val clearButton = findViewById<ImageButton>(R.id.clear_button)
        val backButton = findViewById<ImageButton>(R.id.back_button)
        val tracksRecyclerView = findViewById<RecyclerView>(R.id.recycler_tracks)
        val trackList = ArrayList<Track>()
        trackList.add(Track(getString(R.string.mock_name1), getString(R.string.mock_artist1), getString(R.string.mock_time1), getString(R.string.mock_url1)))
        trackList.add(Track(getString(R.string.mock_name2), getString(R.string.mock_artist2), getString(R.string.mock_time2), getString(R.string.mock_url2)))
        trackList.add(Track(getString(R.string.mock_name3), getString(R.string.mock_artist3), getString(R.string.mock_time3), getString(R.string.mock_url3)))
        trackList.add(Track(getString(R.string.mock_name4), getString(R.string.mock_artist4), getString(R.string.mock_time4), getString(R.string.mock_url4)))
        trackList.add(Track(getString(R.string.mock_name5), getString(R.string.mock_artist5), getString(R.string.mock_time5), getString(R.string.mock_url5)))

        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        val trackListAdapter = TracksAdapter(trackList)

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
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.visibility = clearButtonVisibility(s)
                editTextValue = s.toString()
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
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editTextValue = savedInstanceState.getString(SEARCH_TEXT, TEXT_DEFAULT)
    }

    companion object {
        val SEARCH_TEXT = "SEARCH_TEXT"
        val TEXT_DEFAULT = ""
    }

}