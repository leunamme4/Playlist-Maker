package com.example.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator.getThemeInteractor
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this, SettingsViewModel.getViewModelFactory())[SettingsViewModel::class.java]

        val themeSwitcher = binding.themeSwitcher
        themeSwitcher.isChecked = viewModel.getDarkTheme()

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeChanged(checked)
        }

        //backButton
        val backButton = binding.backButton
        val backClickListener: View.OnClickListener = View.OnClickListener {
            this.finish()
        }
        backButton.setOnClickListener(backClickListener)

        //sharing
        val shareButton = binding.appSharing
        val shareClickListener: View.OnClickListener = View.OnClickListener {
            viewModel.emailIntent()
        }
        shareButton.setOnClickListener(shareClickListener)

        //support
        val supportButton = binding.support
        val supportClickListener: View.OnClickListener = View.OnClickListener {
            viewModel.supportIntent()
        }
        supportButton.setOnClickListener(supportClickListener)

        //agreement
        val agreementButton = binding.agreement
        val agreementClickListener: View.OnClickListener = View.OnClickListener {
            viewModel.agreementIntent()
        }
        agreementButton.setOnClickListener(agreementClickListener)
    }
}