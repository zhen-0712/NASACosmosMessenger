package com.example.line_dev.data.model

import com.google.gson.annotations.SerializedName

data class ApodResponse(
    @SerializedName("date") val date: String,
    @SerializedName("title") val title: String,
    @SerializedName("explanation") val explanation: String,
    @SerializedName("url") val url: String,
    @SerializedName("hdurl") val hdUrl: String?,
    @SerializedName("media_type") val mediaType: String,
    @SerializedName("copyright") val copyright: String?
)