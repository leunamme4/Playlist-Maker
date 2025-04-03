package com.example.playlistmaker.media.ui.playlists.create_playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.api.PlaylistsInteractor
import com.example.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreatePlaylistViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {
    private var newPlaylist = Playlist()

    fun addCover(path: String) {
        newPlaylist.coverPath = path
    }

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    fun addPlaylist(name: String, description: String = "") {
        newPlaylist.name = name
        newPlaylist.description = description
        viewModelScope.launch (Dispatchers.IO) {
            playlistsInteractor.addPlaylist(newPlaylist)
        }
    }

    fun updatePlaylist(name: String, description: String = "") {
        newPlaylist.name = name
        newPlaylist.description = description
        viewModelScope.launch (Dispatchers.IO) {
            playlistsInteractor.updatePlaylist(newPlaylist)
        }
    }

    fun getPlaylistById(id: Int) {
        viewModelScope.launch {
            val editedPlaylist = playlistsInteractor.getPlaylistById(id)
            newPlaylist = editedPlaylist
            _playlist.postValue(editedPlaylist)
        }

    }

    fun getName(): String {
        return newPlaylist.name
    }
}