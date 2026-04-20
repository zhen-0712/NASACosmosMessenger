package com.example.line_dev.viewmodel

import com.example.line_dev.data.model.ApodResponse
import com.example.line_dev.data.repository.ApodRepository
import com.example.line_dev.data.repository.FavoriteRepository
import io.mockk.coEvery
import io.mockk.coVerify
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
class ChatViewModelFavoriteTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var apodRepository: ApodRepository
    private lateinit var favoriteRepository: FavoriteRepository
    private lateinit var viewModel: TestChatViewModel

    private val fakeApod = ApodResponse(
        date = "1995-06-20",
        title = "Test Star",
        explanation = "A test explanation",
        url = "https://example.com/image.jpg",
        hdUrl = null,
        mediaType = "image",
        copyright = null
    )

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
    fun `saveFavorite shows success snackbar when not already favorited`() = testScope.runTest {
        coEvery { favoriteRepository.isFavorite(any()) } returns false
        coEvery { favoriteRepository.addFavorite(any()) } returns Unit
        viewModel.saveFavorite(fakeApod)
        advanceUntilIdle()
        assertEquals("已加入收藏", viewModel.snackbarMessage.value)
    }

    @Test
    fun `saveFavorite shows duplicate snackbar when already favorited`() = testScope.runTest {
        coEvery { favoriteRepository.isFavorite(any()) } returns true
        viewModel.saveFavorite(fakeApod)
        advanceUntilIdle()
        assertEquals("已經在收藏中了", viewModel.snackbarMessage.value)
    }

    @Test
    fun `saveFavorite calls addFavorite when not already favorited`() = testScope.runTest {
        coEvery { favoriteRepository.isFavorite(any()) } returns false
        coEvery { favoriteRepository.addFavorite(any()) } returns Unit
        viewModel.saveFavorite(fakeApod)
        advanceUntilIdle()
        coVerify { favoriteRepository.addFavorite(fakeApod) }
    }

    @Test
    fun `saveFavorite does not call addFavorite when already favorited`() = testScope.runTest {
        coEvery { favoriteRepository.isFavorite(any()) } returns true
        viewModel.saveFavorite(fakeApod)
        advanceUntilIdle()
        coVerify(exactly = 0) { favoriteRepository.addFavorite(any()) }
    }

    @Test
    fun `clearSnackbar sets snackbarMessage to null`() = testScope.runTest {
        coEvery { favoriteRepository.isFavorite(any()) } returns false
        coEvery { favoriteRepository.addFavorite(any()) } returns Unit
        viewModel.saveFavorite(fakeApod)
        advanceUntilIdle()
        viewModel.clearSnackbar()
        assertNull(viewModel.snackbarMessage.value)
    }
}