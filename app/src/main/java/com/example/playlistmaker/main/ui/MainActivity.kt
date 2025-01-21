package com.example.playlistmaker.main.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding
import com.example.playlistmaker.media.ui.MediaActivity
import com.example.playlistmaker.search.ui.SearchActivity
import com.example.playlistmaker.settings.ui.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        //searchListener
        val search = binding.search

        val searchClickListener: View.OnClickListener = View.OnClickListener {
            val navigationIntent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(navigationIntent)
        }

        search.setOnClickListener(searchClickListener)

        //mediaListener
        val media = binding.media

        val mediaClickListener: View.OnClickListener = View.OnClickListener {
            val navigationIntent = Intent(this@MainActivity, MediaActivity::class.java)
            startActivity(navigationIntent)
        }

        media.setOnClickListener(mediaClickListener)


        //mediaListener
        val settings = binding.settings

        val settingsClickListener: View.OnClickListener = View.OnClickListener {
            val navigationIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(navigationIntent)
        }

        settings.setOnClickListener(settingsClickListener)
    }
}