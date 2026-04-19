package com.example.line_dev.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val date: String,
    val title: String,
    val explanation: String,
    val url: String,
    val hdUrl: String?,
    val mediaType: String,
    val copyright: String?,
    val savedAt: Long = System.currentTimeMillis()
)