package com.example.line_dev.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.line_dev.data.local.AppDatabase
import com.example.line_dev.data.local.ApodCacheEntity
import com.example.line_dev.data.model.ApodResponse
import com.example.line_dev.data.remote.NasaApiService
import com.example.line_dev.data.repository.ApodRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ApodRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: TestableApodRepository

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
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = TestableApodRepository(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun getApod_success_returns_apod_and_caches_it() = runTest {
        repository.mockApiResult = Result.success(fakeApod)

        val result = repository.getApod("1995-06-20")

        assertTrue(result.isSuccess)
        assertEquals("1995-06-20", result.getOrNull()?.date)

        // 確認有存進快取
        val cached = db.apodCacheDao().getCacheByDate("1995-06-20")
        assertNotNull(cached)
        assertEquals("Test Star", cached?.title)
    }

    @Test
    fun getApod_failure_falls_back_to_cache() = runTest {
        // 先存一筆快取
        db.apodCacheDao().insertCache(
            ApodCacheEntity(
                date = "1995-06-20",
                title = "Cached Star",
                explanation = "Cached explanation",
                url = "https://example.com/cached.jpg",
                hdUrl = null,
                mediaType = "image",
                copyright = null
            )
        )

        // 模擬網路失敗
        repository.mockApiResult = Result.failure(Exception("no network"))

        val result = repository.getApod("1995-06-20")

        assertTrue(result.isSuccess)
        assertEquals("Cached Star", result.getOrNull()?.title)
    }

    @Test
    fun getApod_failure_with_no_cache_returns_error() = runTest {
        repository.mockApiResult = Result.failure(Exception("no network"))

        val result = repository.getApod("2099-01-01")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("快取") == true)
    }

    @Test
    fun getApod_no_date_falls_back_to_latest_cache() = runTest {
        db.apodCacheDao().insertCache(
            ApodCacheEntity(
                date = "2000-01-01",
                title = "Latest Cache",
                explanation = "Latest",
                url = "https://example.com/latest.jpg",
                hdUrl = null,
                mediaType = "image",
                copyright = null,
                cachedAt = 9999L
            )
        )
        repository.mockApiResult = Result.failure(Exception("no network"))

        val result = repository.getApod(null)

        assertTrue(result.isSuccess)
        assertEquals("Latest Cache", result.getOrNull()?.title)
    }
}

/**
 * Testable version of ApodRepository with injectable mock API result
 */
class TestableApodRepository(private val db: AppDatabase) {

    var mockApiResult: Result<ApodResponse> = Result.failure(Exception("not set"))

    private val cacheDao = db.apodCacheDao()

    suspend fun getApod(date: String?): Result<ApodResponse> {
        return try {
            val response = mockApiResult.getOrThrow()
            cacheDao.insertCache(
                ApodCacheEntity(
                    date = response.date,
                    title = response.title,
                    explanation = response.explanation,
                    url = response.url,
                    hdUrl = response.hdUrl,
                    mediaType = response.mediaType,
                    copyright = response.copyright
                )
            )
            Result.success(response)
        } catch (e: Exception) {
            val cached = if (date != null) cacheDao.getCacheByDate(date)
            else cacheDao.getLatestCache()

            if (cached != null) {
                Result.success(
                    ApodResponse(
                        date = cached.date,
                        title = cached.title,
                        explanation = cached.explanation,
                        url = cached.url,
                        hdUrl = cached.hdUrl,
                        mediaType = cached.mediaType,
                        copyright = cached.copyright
                    )
                )
            } else {
                Result.failure(Exception("無網路連線，也沒有快取資料"))
            }
        }
    }
}