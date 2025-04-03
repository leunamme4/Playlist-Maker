package com.example.playlistmaker.media.data

import com.example.playlistmaker.db.entity.PlaylistEntity
import com.example.playlistmaker.db.entity.TrackEntity
import com.example.playlistmaker.media.domain.models.Playlist
import com.example.playlistmaker.search.domain.models.Track

class PlaylistConverter {
    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
            playlist.name,
            playlist.description,
            playlist.coverPath,
            playlist.trackListIds,
            playlist.tracksCount
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.id,
            playlistEntity.name,
            playlistEntity.description,
            playlistEntity.coverPath,
            playlistEntity.trackListIds,
            playlistEntity.tracksCount
        )
    }
}