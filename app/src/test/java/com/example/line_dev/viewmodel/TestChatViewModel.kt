package com.example.line_dev.viewmodel

import com.example.line_dev.data.model.ApodResponse
import com.example.line_dev.data.model.ChatMessage
import com.example.line_dev.data.model.DateParser
import com.example.line_dev.data.repository.ApodRepository
import com.example.line_dev.data.repository.FavoriteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Testable version of ChatViewModel with injected repositories (no Application context needed)
 */
class TestChatViewModel(
    private val apodRepo: ApodRepository,
    private val favoriteRepo: FavoriteRepository,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage

    init {
        appendMessage(ChatMessage(content = "歡迎！輸入任何日期，我會告訴你那天宇宙長什麼樣子。", isUser = false))
    }

    fun sendMessage(input: String) {
        if (input.isBlank()) return
        appendMessage(ChatMessage(content = input, isUser = true))
        val date = DateParser.extractDate(input)
        scope.launch {
            _isLoading.value = true
            val result = apodRepo.getApod(date)
            result.onSuccess { apod ->
                val content = if (apod.mediaType == "video")
                    "那天的宇宙是一段影片：\n${apod.title}\n${apod.url}"
                else "那天宇宙長這樣..."
                appendMessage(ChatMessage(content = content, isUser = false, apod = apod))
            }.onFailure { e ->
                appendMessage(ChatMessage(content = e.message ?: "抱歉，無法取得資料", isUser = false))
            }
            _isLoading.value = false
        }
    }

    fun saveFavorite(apod: ApodResponse) {
        scope.launch {
            val already = favoriteRepo.isFavorite(apod.date)
            if (already) {
                _snackbarMessage.value = "已經在收藏中了"
            } else {
                favoriteRepo.addFavorite(apod)
                _snackbarMessage.value = "已加入收藏"
            }
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    private fun appendMessage(message: ChatMessage) {
        _messages.value = _messages.value + message
    }
}