package com.example.playlistmaker.media.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistsInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {
    private val newPlaylist = Playlist()

    fun addCover(path: String) {
        newPlaylist.coverPath = path
    }

    fun addPlaylist(name: String, description: String = "") {
        newPlaylist.name = name
        newPlaylist.description = description
        viewModelScope.launch (Dispatchers.IO) {
            playlistsInteractor.addPlaylist(newPlaylist)
        }
    }

    fun checkEmpty(): Boolean{
        return newPlaylist.name.isNotEmpty() || newPlaylist.description.isNotEmpty() || !newPlaylist.coverPath.isNullOrEmpty()
    }

    fun getName(): String {
        return newPlaylist.name
    }
}