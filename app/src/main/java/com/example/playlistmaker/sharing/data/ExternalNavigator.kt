package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

class ExternalNavigator(val context: Context) {

    fun emailIntent() {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = context.getString(R.string.sharing_type)
        val message = context.getString(R.string.practicum_url)
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(shareIntent)
    }

    fun playlistShare(tracks: List<Track>, playlist: Playlist) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = context.getString(R.string.sharing_type)
        var trackList = ""
        for(i in tracks.indices) {
            val track = "${i+1}. ${tracks[i].artistName} - ${tracks[i].trackName} (${tracks[i].trackTimeMillis})\n"
            trackList += track
        }
        val string = "${playlist.name}\n${playlist.description}\n${tracks.size} ${trackCountFormat(tracks.size)}\n"
        val message = string + trackList
        shareIntent.putExtra(Intent.EXTRA_TEXT, message)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(shareIntent)
    }

    private fun trackCountFormat(count: Int): String {
        return when(count % 10) {
            0, in 5..9 -> "треков"
            1 -> "трек"
            in 2..4 -> "трека"
            else -> {""}
        }
    }

    fun supportIntent() {
        val supportIntent = Intent(Intent.ACTION_SENDTO)
        val message = context.getString(R.string.support_message)
        val subject = context.getString(R.string.support_subject)
        supportIntent.data = Uri.parse(context.getString(R.string.support_data_type))
        supportIntent.putExtra(
            Intent.EXTRA_EMAIL,
            arrayOf(context.getString(R.string.support_my_mail))
        )
        supportIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        supportIntent.putExtra(Intent.EXTRA_TEXT, message)
        supportIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        supportIntent.addCategory(Intent.CATEGORY_DEFAULT)

        context.startActivity(supportIntent)
    }

    fun agreementIntent() {
        val agreementIntent =
            Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.agreement_offer)))
        agreementIntent.addCategory(Intent.CATEGORY_DEFAULT)
        agreementIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        context.startActivity(agreementIntent)
    }
}