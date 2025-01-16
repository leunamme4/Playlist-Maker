package com.example.playlistmaker.data

import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TracksSearchResponse
import retrofit2.Callback

interface NetworkClient {
    fun doRequest(dto: Any): Response
}