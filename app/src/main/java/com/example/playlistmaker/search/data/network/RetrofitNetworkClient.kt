package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TracksSearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.getKoin
import java.io.IOException

class RetrofitNetworkClient(private val tracksApiService: TracksApiService) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        val response: Response = getKoin().get()

        if (dto is TracksSearchRequest) {
            return withContext(Dispatchers.IO) {
                try {
                    val resp = tracksApiService.search(dto.expression)
                    resp.apply { resultCode = 200 }
                } catch (exception: IOException) {
                    response.apply { resultCode = 400 }
                }
            }
        } else {
            return response.apply { resultCode = 400 }
        }
    }
}