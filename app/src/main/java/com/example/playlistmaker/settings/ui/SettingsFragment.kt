package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.data.CheckedState
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initChecked()

        val themeSwitcher = binding.themeSwitcher
        viewModel.observeDarkTheme().observe(viewLifecycleOwner) {
            if (it !is CheckedState.None) {
                themeSwitcher.isChecked = it is CheckedState.Checked
                viewModel.setState(CheckedState.None)
            }
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            viewModel.onThemeChanged(checked)
        }

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