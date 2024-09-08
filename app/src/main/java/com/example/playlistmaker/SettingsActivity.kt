package com.example.playlistmaker

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backButton = findViewById<ImageButton>(R.id.back_button)
        val backClickListener: View.OnClickListener = View.OnClickListener {
            val navigationIntent = Intent(this@SettingsActivity, MainActivity::class.java)
            startActivity(navigationIntent)
        }

        backButton.setOnClickListener(backClickListener)
    }
}