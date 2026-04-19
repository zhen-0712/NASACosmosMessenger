package com.example.line_dev.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.line_dev.data.model.ChatMessage
import com.example.line_dev.data.model.DateParser
import com.example.line_dev.data.model.ApodResponse
import com.example.line_dev.data.repository.ApodRepository
import com.example.line_dev.data.repository.FavoriteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val apodRepository = ApodRepository()
    private val favoriteRepository = FavoriteRepository(application)

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    init {
        addNovaMessage("歡迎！輸入任何日期，我會告訴你那天宇宙長什麼樣子。")
    }

    fun sendMessage(input: String) {
        if (input.isBlank()) return
        val userMessage = ChatMessage(content = input, isUser = true)
        appendMessage(userMessage)
        val date = DateParser.extractDate(input)
        fetchApod(date)
    }

    private fun fetchApod(date: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = apodRepository.getApod(date)
            result.onSuccess { apod ->
                val content = if (apod.mediaType == "video") {
                    "那天的宇宙是一段影片：\n${apod.title}\n${apod.url}"
                } else {
                    "那天宇宙長這樣..."
                }
                appendMessage(ChatMessage(content = content, isUser = false, apod = apod))
            }.onFailure { e ->
                addNovaMessage(e.message ?: "抱歉，無法取得資料，請確認日期格式或網路連線。")
            }
            _isLoading.value = false
        }
    }

    fun saveFavorite(apod: ApodResponse) {
        viewModelScope.launch {
            val already = favoriteRepository.isFavorite(apod.date)
            if (already) {
                _snackbarMessage.value = "已經在收藏中了"
            } else {
                favoriteRepository.addFavorite(apod)
                _snackbarMessage.value = "已加入收藏"
            }
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    private fun addNovaMessage(text: String) {
        appendMessage(ChatMessage(content = text, isUser = false))
    }

    private fun appendMessage(message: ChatMessage) {
        _messages.value = _messages.value + message
    }
}