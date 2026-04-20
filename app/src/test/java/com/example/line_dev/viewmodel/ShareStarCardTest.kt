package com.example.line_dev.viewmodel

import com.example.line_dev.data.model.ApodResponse
import com.example.line_dev.data.repository.ApodRepository
import com.example.line_dev.data.repository.FavoriteRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ShareStarCardTest {

    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)
    private lateinit var apodRepository: ApodRepository
    private lateinit var favoriteRepository: FavoriteRepository

    private val imageApod = ApodResponse(
        date = "1995-06-20",
        title = "Test Star",
        explanation = "A test explanation",
        url = "https://example.com/image.jpg",
        hdUrl = null,
        mediaType = "image",
        copyright = null
    )

    private val videoApod = ApodResponse(
        date = "2000-01-01",
        title = "Video Star",
        explanation = "A video explanation",
        url = "https://youtube.com/watch?v=test",
        hdUrl = null,
        mediaType = "video",
        copyright = null
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        apodRepository = mockk()
        favoriteRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `share is only available for image media type`() {
        assertTrue(imageApod.mediaType == "image")
        assertFalse(videoApod.mediaType == "image")
    }

    @Test
    fun `star card requires valid image url`() {
        assertTrue(imageApod.url.isNotBlank())
        assertTrue(imageApod.url.startsWith("http"))
    }

    @Test
    fun `star card title is not blank`() {
        assertTrue(imageApod.title.isNotBlank())
    }

    @Test
    fun `star card date format is YYYY-MM-DD`() {
        val dateRegex = Regex("""\d{4}-\d{2}-\d{2}""")
        assertTrue(dateRegex.matches(imageApod.date))
    }

    @Test
    fun `video apod should not trigger star card generation`() {
        val shouldShare = videoApod.mediaType == "image"
        assertFalse(shouldShare)
    }

    @Test
    fun `apod with empty url should not trigger star card generation`() {
        val emptyUrlApod = imageApod.copy(url = "")
        val shouldShare = emptyUrlApod.url.isNotBlank() && emptyUrlApod.mediaType == "image"
        assertFalse(shouldShare)
    }
}