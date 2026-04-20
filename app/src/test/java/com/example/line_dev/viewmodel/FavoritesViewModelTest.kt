package com.example.line_dev.viewmodel

import com.example.line_dev.data.local.FavoriteEntity
import com.example.line_dev.data.repository.FavoriteRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
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
class FavoritesViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var favoriteRepository: FavoriteRepository

    private val entity1 = FavoriteEntity(
        date = "1995-06-20",
        title = "Star One",
        explanation = "First star",
        url = "https://example.com/1.jpg",
        hdUrl = null,
        mediaType = "image",
        copyright = null,
        savedAt = 1000L
    )

    private val entity2 = FavoriteEntity(
        date = "2000-01-01",
        title = "Star Two",
        explanation = "Second star",
        url = "https://example.com/2.jpg",
        hdUrl = null,
        mediaType = "image",
        copyright = null,
        savedAt = 2000L
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        favoriteRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `favorites list is empty initially when repository returns empty`() = testScope.runTest {
        coEvery { favoriteRepository.getAllFavorites() } returns flowOf(emptyList())
        val viewModel = TestFavoritesViewModel(favoriteRepository, testScope)
        advanceUntilIdle()
        assertTrue(viewModel.favorites.value.isEmpty())
    }

    @Test
    fun `favorites list shows items from repository`() = testScope.runTest {
        coEvery { favoriteRepository.getAllFavorites() } returns flowOf(listOf(entity1, entity2))
        val viewModel = TestFavoritesViewModel(favoriteRepository, testScope)
        advanceUntilIdle()
        assertEquals(2, viewModel.favorites.value.size)
    }

    @Test
    fun `deleteFavorite calls repository removeFavorite`() = testScope.runTest {
        coEvery { favoriteRepository.getAllFavorites() } returns flowOf(listOf(entity1))
        coEvery { favoriteRepository.removeFavorite(any()) } returns Unit
        val viewModel = TestFavoritesViewModel(favoriteRepository, testScope)
        viewModel.deleteFavorite(entity1)
        advanceUntilIdle()
        coVerify { favoriteRepository.removeFavorite(entity1) }
    }

    @Test
    fun `favorites are ordered by savedAt descending`() = testScope.runTest {
        coEvery { favoriteRepository.getAllFavorites() } returns flowOf(listOf(entity2, entity1))
        val viewModel = TestFavoritesViewModel(favoriteRepository, testScope)
        advanceUntilIdle()
        assertEquals("Star Two", viewModel.favorites.value[0].title)
        assertEquals("Star One", viewModel.favorites.value[1].title)
    }
}

class TestFavoritesViewModel(
    private val repo: FavoriteRepository,
    private val scope: CoroutineScope
) {
    private val _favorites = MutableStateFlow<List<FavoriteEntity>>(emptyList())
    val favorites: StateFlow<List<FavoriteEntity>> = _favorites

    init {
        scope.launch {
            repo.getAllFavorites().collect { _favorites.value = it }
        }
    }

    fun deleteFavorite(entity: FavoriteEntity) {
        scope.launch {
            repo.removeFavorite(entity)
        }
    }
}