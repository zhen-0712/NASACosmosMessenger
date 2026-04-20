package com.example.line_dev.data.repository

import android.content.Context
import com.example.line_dev.BuildConfig
import com.example.line_dev.data.local.AppDatabase
import com.example.line_dev.data.local.ApodCacheEntity
import com.example.line_dev.data.model.ApodResponse
import com.example.line_dev.data.remote.RetrofitInstance

class ApodRepository(context: Context) {
    private val api = RetrofitInstance.api
    private val apiKey = BuildConfig.NASA_API_KEY
    private val cacheDao = AppDatabase.getDatabase(context).apodCacheDao()

    suspend fun getApod(date: String? = null): Result<ApodResponse> {
        return try {
            // 先嘗試網路請求
            val response = api.getApod(apiKey = apiKey, date = date)
            // 成功就存進快取
            cacheDao.insertCache(
                ApodCacheEntity(
                    date = response.date,
                    title = response.title,
                    explanation = response.explanation,
                    url = response.url,
                    hdUrl = response.hdUrl,
                    mediaType = response.mediaType,
                    copyright = response.copyright
                )
            )
            Result.success(response)
        } catch (e: retrofit2.HttpException) {
            if (e.code() == 400) {
                Result.failure(Exception("日期須介於 1995/06/16 至今"))
            } else {
                // 網路錯誤時嘗試從快取取
                fallbackToCache(date)
            }
        } catch (e: Exception) {
            // 離線時從快取取
            fallbackToCache(date)
        }
    }

    private suspend fun fallbackToCache(date: String?): Result<ApodResponse> {
        val cached = if (date != null) {
            cacheDao.getCacheByDate(date)
        } else {
            cacheDao.getLatestCache()
        }
        return if (cached != null) {
            Result.success(
                ApodResponse(
                    date = cached.date,
                    title = cached.title,
                    explanation = cached.explanation,
                    url = cached.url,
                    hdUrl = cached.hdUrl,
                    mediaType = cached.mediaType,
                    copyright = cached.copyright
                )
            )
        } else {
            Result.failure(Exception("無網路連線，也沒有快取資料"))
        }
    }
}