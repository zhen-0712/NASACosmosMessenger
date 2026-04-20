package com.example.line_dev.viewmodel

import com.example.line_dev.data.model.ApodResponse
import com.example.line_dev.data.repository.ApodRepository
import com.example.line_dev.data.repository.FavoriteRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelMessageTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var apodRepository: ApodRepository
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var viewModel: TestChatViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apodRepository = mockk()
        favoriteRepository = mockk()
        viewModel = TestChatViewModel(apodRepository, favoriteRepository, testScope)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial message is welcome message from Nova`() {
        val messages = viewModel.messages.value
        assertEquals(1, messages.size)
        assertFalse(messages[0].isUser)
        assertTrue(messages[0].content.contains("歡迎"))
    }

    @Test
    fun `sendMessage adds user message to list`() = testScope.runTest {
        coEvery { apodRepository.getApod(any()) } returns Result.failure(Exception("no network"))
        viewModel.sendMessage("1995/06/20")
        advanceUntilIdle()
        assertTrue(viewModel.messages.value.any { it.isUser && it.content == "1995/06/20" })
    }

    @Test
    fun `sendMessage with blank input does nothing`() = testScope.runTest {
        val before = viewModel.messages.value.size
        viewModel.sendMessage("   ")
        advanceUntilIdle()
        assertEquals(before, viewModel.messages.value.size)
    }

    @Test
    fun `successful API response adds Nova message with apod`() = testScope.runTest {
        val fakeApod = ApodResponse(
            date = "1995-06-20",
            title = "Test Star",
            explanation = "A test explanation",
            url = "https://example.com/image.jpg",
            hdUrl = null,
            mediaType = "image",
            copyright = null
        )
        coEvery { apodRepository.getApod(any()) } returns Result.success(fakeApod)
        viewModel.sendMessage("1995/06/20")
        advanceUntilIdle()
        val novaReply = viewModel.messages.value.last()
        assertFalse(novaReply.isUser)
        assertNotNull(novaReply.apod)
        assertEquals("1995-06-20", novaReply.apod?.date)
    }

    @Test
    fun `failed API response adds error message from Nova`() = testScope.runTest {
        coEvery { apodRepository.getApod(any()) } returns Result.failure(Exception("無網路連線"))
        viewModel.sendMessage("1995/06/20")
        advanceUntilIdle()
        val novaReply = viewModel.messages.value.last()
        assertFalse(novaReply.isUser)
        assertTrue(novaReply.content.contains("無網路連線"))
    }

    @Test
    fun `video media type shows link message`() = testScope.runTest {
        val fakeVideo = ApodResponse(
            date = "2000-01-01",
            title = "Video Test",
            explanation = "A video",
            url = "https://youtube.com/watch?v=test",
            hdUrl = null,
            mediaType = "video",
            copyright = null
        )
        coEvery { apodRepository.getApod(any()) } returns Result.success(fakeVideo)
        viewModel.sendMessage("2000/01/01")
        advanceUntilIdle()
        val novaReply = viewModel.messages.value.last()
        assertTrue(novaReply.content.contains("影片"))
    }
}