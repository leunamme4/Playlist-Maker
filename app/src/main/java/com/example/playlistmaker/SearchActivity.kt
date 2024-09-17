package com.example.playlistmaker

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton

class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val searchEditText = findViewById<EditText>(R.id.search_edit)
        val clearButton = findViewById<ImageButton>(R.id.clear_button)

        if (savedInstanceState != null) {
            searchEditText.setText(editTextValue)
        }

        //backButton
        val backButton = findViewById<ImageButton>(R.id.back_button)
        val backClickListener: View.OnClickListener = View.OnClickListener {
            val navigationIntent = Intent(this@SearchActivity, MainActivity::class.java)
            startActivity(navigationIntent)
        }
        backButton.setOnClickListener(backClickListener)

        //clear
        clearButton.setOnClickListener {
            searchEditText.setText("")
            //searchEditText.clearFocus()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        //searchEditText click
        searchEditText.setOnClickListener {
            searchEditText.requestFocus()
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

    var editTextValue = TEXT_DEFAULT

    companion object {
        val SEARCH_TEXT = "SEARCH_TEXT"
        val TEXT_DEFAULT = ""
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, editTextValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        editTextValue = savedInstanceState.getString(SEARCH_TEXT, TEXT_DEFAULT)
    }

}