package com.example.playlistmaker.root

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityRootBinding
import com.example.playlistmaker.db.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Привязываем вёрстку к экрану
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.rootFragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController


        val bottomNavigationView = binding.bottomNavigationView
        bottomNavigationView.setupWithNavController(navController)

    }

    override fun onDestroy() {
        if (isFinishing) GlobalScope.launch (Dispatchers.Main) {
            val db: AppDatabase = getKoin().get()
            db.playlistsDao().clearPlaylists()
            db.TracksDao().clearFavorites()
            db.tracksPlaylistDao().clearPlaylistsTracks()
        }
        super.onDestroy()
    }

    fun hideNavigationBar() {
        if (binding.bottomNavigationView != null) {
            binding.border.visibility = View.GONE
            binding.bottomNavigationView.visibility = View.GONE
        }
    }

    fun showNavigationBar() {
        if (binding.bottomNavigationView != null) {
            binding.border.visibility = View.VISIBLE
            binding.bottomNavigationView.visibility = View.VISIBLE
        }
    }
}