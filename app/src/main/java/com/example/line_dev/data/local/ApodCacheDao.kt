package com.example.line_dev.data.local

import androidx.room.*

@Dao
interface ApodCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(apod: ApodCacheEntity)

    @Query("SELECT * FROM apod_cache WHERE date = :date LIMIT 1")
    suspend fun getCacheByDate(date: String): ApodCacheEntity?

    @Query("SELECT * FROM apod_cache ORDER BY cachedAt DESC LIMIT 1")
    suspend fun getLatestCache(): ApodCacheEntity?
}