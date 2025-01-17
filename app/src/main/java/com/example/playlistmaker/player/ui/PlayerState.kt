package com.example.playlistmaker.player.ui

sealed class PlayerState {
    object Created: PlayerState()
    object Prepared : PlayerState()
    data class Paused (val time: CharSequence) : PlayerState()
    data class Playing(val time: CharSequence) : PlayerState()
}