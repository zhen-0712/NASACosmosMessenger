package com.example.line_dev.data.model

data class ChatMessage(
    val id: Long = System.currentTimeMillis(),
    val content: String,
    val isUser: Boolean,
    val apod: ApodResponse? = null
)