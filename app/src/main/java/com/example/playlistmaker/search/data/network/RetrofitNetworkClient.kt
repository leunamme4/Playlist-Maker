package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import org.koin.java.KoinJavaComponent.getKoin
import java.io.IOException

class RetrofitNetworkClient (private val tracksApiService: TracksApiService) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        val response: Response = getKoin().get()
        if (dto is TracksSearchRequest) {
            try {
                val resp = tracksApiService.search(dto.expression).execute()
                val body = resp.body() ?: response
                return body.apply { resultCode = resp.code() }
            } catch (exception: IOException) {
                return response.apply { resultCode = 400 }
            }
        } else {
            return response.apply { resultCode = 400 }
        }
    }
}