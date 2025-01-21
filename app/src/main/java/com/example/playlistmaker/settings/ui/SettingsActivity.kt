package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.data.CheckedState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {

    private val viewModel by viewModel<SettingsViewModel>()
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d("theme", "theme")
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.initChecked()

        val themeSwitcher = binding.themeSwitcher
        viewModel.observeDarkTheme().observe(this) {
            if (it !is CheckedState.None) {
                themeSwitcher.isChecked = it is CheckedState.Checked
                viewModel.setState(CheckedState.None)
            }
        }

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