package com.example.playlistmaker.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.Creator.getThemeInteractor
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {

    private val themeInteractor = getThemeInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeSwitcher = findViewById<SwitchMaterial>(R.id.theme_switcher)
        themeSwitcher.isChecked = getDarkTheme()

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            onThemeChanged(checked)
        }

        //backButton
        val backButton = findViewById<ImageButton>(R.id.back_button)
        val backClickListener: View.OnClickListener = View.OnClickListener {
            this.finish()
        }
        backButton.setOnClickListener(backClickListener)

        //sharing
        val shareButton = findViewById<FrameLayout>(R.id.app_sharing)
        val shareClickListener: View.OnClickListener = View.OnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.type = getString(R.string.sharing_type)
            val message = getString(R.string.practicum_url)
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(shareIntent)
        }
        shareButton.setOnClickListener(shareClickListener)

        //support
        val supportButton = findViewById<FrameLayout>(R.id.support)
        val supportClickListener: View.OnClickListener = View.OnClickListener {
            val supportIntent = Intent(Intent.ACTION_SENDTO)
            val message = getString(R.string.support_message)
            val subject = getString(R.string.support_subject)
            supportIntent.data = Uri.parse(getString(R.string.support_data_type))
            supportIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_my_mail)))
            supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            supportIntent.putExtra(Intent.EXTRA_TEXT, message)
            supportIntent.addCategory(Intent.CATEGORY_DEFAULT)
            startActivity(supportIntent)
        }
        supportButton.setOnClickListener(supportClickListener)

        //agreement
        val agreementButton = findViewById<FrameLayout>(R.id.agreement)
        val agreementClickListener: View.OnClickListener = View.OnClickListener {
            val agreementIntent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.agreement_offer)))
            agreementIntent.addCategory(Intent.CATEGORY_DEFAULT)
            startActivity(agreementIntent)
        }
        agreementButton.setOnClickListener(agreementClickListener)
    }

    private fun getDarkTheme() : Boolean {
        return themeInteractor.getDarkTheme()
    }

    private fun onThemeChanged(checked: Boolean) {
        themeInteractor.onThemeChange(checked)
    }
}