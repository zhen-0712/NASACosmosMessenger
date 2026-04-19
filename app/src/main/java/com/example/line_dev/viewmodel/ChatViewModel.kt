package com.example.line_dev.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.line_dev.data.model.ApodResponse
import com.example.line_dev.data.model.ChatMessage
import com.example.line_dev.data.model.DateParser
import com.example.line_dev.data.repository.ApodRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val repository = ApodRepository()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

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
            val result = repository.getApod(date)
            result.onSuccess { apod ->
                val content = if (apod.mediaType == "video") {
                    "那天的宇宙是一段影片：\n${apod.title}\n${apod.url}"
                } else {
                    "那天宇宙長這樣..."
                }
                appendMessage(ChatMessage(content = content, isUser = false, apod = apod))
            }.onFailure {
                addNovaMessage("抱歉，無法取得資料，請確認日期格式或網路連線。")
            }
            _isLoading.value = false
        }
    }

    private fun addNovaMessage(text: String) {
        appendMessage(ChatMessage(content = text, isUser = false))
    }

    private fun appendMessage(message: ChatMessage) {
        _messages.value = _messages.value + message
    }
}