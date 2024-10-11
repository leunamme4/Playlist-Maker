package com.example.playlistmaker

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TracksApiService {
    @GET("/search?entity=song ")
    fun search(@Query("term") text: String) : Call<TrackList>
}