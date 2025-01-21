package com.example.playlistmaker.settings.data

sealed class CheckedState {
    data object Checked: CheckedState()
    data object NonChecked: CheckedState()
    data object None: CheckedState()
}