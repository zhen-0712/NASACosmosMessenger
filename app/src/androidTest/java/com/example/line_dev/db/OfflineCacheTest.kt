package com.example.line_dev.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.line_dev.data.local.AppDatabase
import com.example.line_dev.data.local.ApodCacheEntity
import com.example.line_dev.data.model.ApodResponse
import com.example.line_dev.repository.FakeApodRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class OfflineCacheTest {

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun offline_with_cached_data_returns_cached_apod() = runTest {
        // 預先存入快取
        db.apodCacheDao().insertCache(
            ApodCacheEntity(
                date = "1995-06-20",
                title = "Offline Cached Star",
                explanation = "Cached while online",
                url = "https://example.com/cached.jpg",
                hdUrl = null,
                mediaType = "image",
                copyright = null
            )
        )
        // 模擬離線
        val repo = FakeApodRepository(db, Result.failure(Exception("no network")))
        val result = repo.getApod("1995-06-20")

        assertTrue(result.isSuccess)
        assertEquals("Offline Cached Star", result.getOrNull()?.title)
    }

    @Test
    fun offline_without_cache_returns_error_message() = runTest {
        val repo = FakeApodRepository(db, Result.failure(Exception("no network")))
        val result = repo.getApod("2099-01-01")

        assertTrue(result.isFailure)
        assertEquals("無網路連線，也沒有快取資料", result.exceptionOrNull()?.message)
    }

    @Test
    fun online_then_offline_uses_previously_cached_data() = runTest {
        val fakeApod = ApodResponse(
            date = "2000-06-15",
            title = "Online Star",
            explanation = "Fetched while online",
            url = "https://example.com/online.jpg",
            hdUrl = null,
            mediaType = "image",
            copyright = null
        )

        // 第一次：線上成功，存入快取
        val onlineRepo = FakeApodRepository(db, Result.success(fakeApod))
        onlineRepo.getApod("2000-06-15")

        // 第二次：離線，從快取取
        val offlineRepo = FakeApodRepository(db, Result.failure(Exception("no network")))
        val result = offlineRepo.getApod("2000-06-15")

        assertTrue(result.isSuccess)
        assertEquals("Online Star", result.getOrNull()?.title)
    }

    @Test
    fun multiple_cached_items_latest_returned_when_no_date() = runTest {
        db.apodCacheDao().insertCache(
            ApodCacheEntity(
                date = "1995-06-20",
                title = "Old Cache",
                explanation = "Old",
                url = "https://example.com/old.jpg",
                hdUrl = null,
                mediaType = "image",
                copyright = null,
                cachedAt = 1000L
            )
        )
        db.apodCacheDao().insertCache(
            ApodCacheEntity(
                date = "2000-01-01",
                title = "New Cache",
                explanation = "New",
                url = "https://example.com/new.jpg",
                hdUrl = null,
                mediaType = "image",
                copyright = null,
                cachedAt = 9999L
            )
        )

        val repo = FakeApodRepository(db, Result.failure(Exception("no network")))
        val result = repo.getApod(null)

        assertTrue(result.isSuccess)
        assertEquals("New Cache", result.getOrNull()?.title)
    }
}