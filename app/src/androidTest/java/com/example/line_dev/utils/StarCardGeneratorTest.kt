package com.example.line_dev.utils

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class StarCardGeneratorTest {

    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun generateStarCard_with_valid_url_returns_file() = runTest {
        val file = StarCardGenerator.generateStarCard(
            context = context,
            imageUrl = "https://apod.nasa.gov/apod/image/9506/ae_aquilae_royer.gif",
            title = "Test Star",
            date = "1995-06-20"
        )
        assertNotNull(file)
        assertTrue(file!!.exists())
        assertTrue(file.length() > 0)
    }

    @Test
    fun generateStarCard_output_is_jpeg_file() = runTest {
        val file = StarCardGenerator.generateStarCard(
            context = context,
            imageUrl = "https://apod.nasa.gov/apod/image/9506/ae_aquilae_royer.gif",
            title = "Test Star",
            date = "1995-06-20"
        )
        assertNotNull(file)
        assertTrue(file!!.name.endsWith(".jpg"))
    }

    @Test
    fun generateStarCard_filename_contains_date() = runTest {
        val date = "1995-06-20"
        val file = StarCardGenerator.generateStarCard(
            context = context,
            imageUrl = "https://apod.nasa.gov/apod/image/9506/ae_aquilae_royer.gif",
            title = "Test Star",
            date = date
        )
        assertNotNull(file)
        assertTrue(file!!.name.contains(date))
    }

    @Test
    fun generateStarCard_is_saved_in_cache_directory() = runTest {
        val file = StarCardGenerator.generateStarCard(
            context = context,
            imageUrl = "https://apod.nasa.gov/apod/image/9506/ae_aquilae_royer.gif",
            title = "Test Star",
            date = "1995-06-20"
        )
        assertNotNull(file)
        assertTrue(file!!.absolutePath.contains(context.cacheDir.absolutePath))
    }

    @Test
    fun generateStarCard_with_invalid_url_returns_null() = runTest {
        val file = StarCardGenerator.generateStarCard(
            context = context,
            imageUrl = "https://invalid.url.that.does.not.exist/image.jpg",
            title = "Test Star",
            date = "1995-06-20"
        )
        assertNull(file)
    }

    @Test
    fun generateStarCard_cache_directory_is_created() = runTest {
        StarCardGenerator.generateStarCard(
            context = context,
            imageUrl = "https://apod.nasa.gov/apod/image/9506/ae_aquilae_royer.gif",
            title = "Test Star",
            date = "1995-06-20"
        )
        val cacheDir = File(context.cacheDir, "star_cards")
        assertTrue(cacheDir.exists())
        assertTrue(cacheDir.isDirectory)
    }
}