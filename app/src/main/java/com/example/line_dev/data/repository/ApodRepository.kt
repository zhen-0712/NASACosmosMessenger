package com.example.line_dev.data.repository

import com.example.line_dev.data.model.ApodResponse
import com.example.line_dev.data.remote.RetrofitInstance

class ApodRepository {
    private val api = RetrofitInstance.api
    private val apiKey = com.example.line_dev.BuildConfig.NASA_API_KEY

    suspend fun getApod(date: String? = null): Result<ApodResponse> {
      return try {
          val response = api.getApod(apiKey = apiKey, date = date)
          Result.success(response)
      } catch (e: retrofit2.HttpException) {
          if (e.code() == 400) {
              Result.failure(Exception("日期須介於 1995/06/16 至今"))
          } else {
              Result.failure(e)
          }
      } catch (e: Exception) {
          Result.failure(e)
      }
  }
}