package com.example.line_dev.data.repository

import com.example.line_dev.data.model.ApodResponse
import com.example.line_dev.data.remote.RetrofitInstance

class ApodRepository {
    private val api = RetrofitInstance.api
    private val apiKey = "MQfkHXRZCPoZ0tTSag3y6vg3LxfNNJOdeNoFuDR9"

    suspend fun getApod(date: String? = null): Result<ApodResponse> {
        return try {
            val response = api.getApod(apiKey = apiKey, date = date)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}