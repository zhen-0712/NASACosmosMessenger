package com.example.line_dev.data.repository

import com.example.line_dev.data.local.AppDatabase
import com.example.line_dev.data.local.FavoriteEntity
import com.example.line_dev.data.model.ApodResponse
import android.content.Context
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(context: Context) {
    private val dao = AppDatabase.getDatabase(context).favoriteDao()

    fun getAllFavorites(): Flow<List<FavoriteEntity>> = dao.getAllFavorites()

    suspend fun addFavorite(apod: ApodResponse) {
        dao.insertFavorite(
            FavoriteEntity(
                date = apod.date,
                title = apod.title,
                explanation = apod.explanation,
                url = apod.url,
                hdUrl = apod.hdUrl,
                mediaType = apod.mediaType,
                copyright = apod.copyright
            )
        )
    }

    suspend fun removeFavorite(entity: FavoriteEntity) {
        dao.deleteFavorite(entity)
    }

    suspend fun isFavorite(date: String): Boolean {
        return dao.isFavorite(date)
    }
}