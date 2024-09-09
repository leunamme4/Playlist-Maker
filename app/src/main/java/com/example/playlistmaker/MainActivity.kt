package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //searchListener
        val search = findViewById<Button>(R.id.search)

        val searchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val navigationIntent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(navigationIntent)
            }
        }

        search.setOnClickListener(searchClickListener)

        //mediaListener
        val media = findViewById<Button>(R.id.media)

        val mediaClickListener: View.OnClickListener = View.OnClickListener {
            val navigationIntent = Intent(this@MainActivity, MediaActivity::class.java)
            startActivity(navigationIntent)
        }

        media.setOnClickListener(mediaClickListener)


        //mediaListener
        val settings = findViewById<Button>(R.id.settings)

        val settingsClickListener: View.OnClickListener = View.OnClickListener {
            val navigationIntent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(navigationIntent)
        }

        settings.setOnClickListener(settingsClickListener)
    }
}