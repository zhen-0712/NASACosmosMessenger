package com.example.line_dev.data.remote

import com.example.line_dev.data.model.ApodResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaApiService {
    @GET("planetary/apod")
    suspend fun getApod(
        @Query("api_key") apiKey: String,
        @Query("date") date: String? = null
    ): ApodResponse
}