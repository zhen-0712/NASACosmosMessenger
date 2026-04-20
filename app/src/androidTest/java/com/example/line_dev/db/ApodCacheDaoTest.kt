package com.example.line_dev.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.line_dev.data.local.AppDatabase
import com.example.line_dev.data.local.FavoriteDao
import com.example.line_dev.data.local.FavoriteEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class ApodCacheDaoTest {

    private lateinit var db: AppDatabase
    private lateinit var dao: FavoriteDao

    private val testEntity = FavoriteEntity(
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
        dao = db.favoriteDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertFavorite_and_getAllFavorites_returns_inserted_item() = runTest {
        dao.insertFavorite(testEntity)
        val favorites = dao.getAllFavorites().first()
        assertEquals(1, favorites.size)
        assertEquals("1995-06-20", favorites[0].date)
    }

    @Test
    fun deleteFavorite_removes_item_from_list() = runTest {
        dao.insertFavorite(testEntity)
        dao.deleteFavorite(testEntity)
        val favorites = dao.getAllFavorites().first()
        assertTrue(favorites.isEmpty())
    }

    @Test
    fun isFavorite_returns_true_when_exists() = runTest {
        dao.insertFavorite(testEntity)
        assertTrue(dao.isFavorite("1995-06-20"))
    }

    @Test
    fun isFavorite_returns_false_when_not_exists() = runTest {
        assertFalse(dao.isFavorite("2000-01-01"))
    }

    @Test
    fun insertFavorite_with_same_date_replaces_existing() = runTest {
        dao.insertFavorite(testEntity)
        val updated = testEntity.copy(title = "Updated Title")
        dao.insertFavorite(updated)
        val favorites = dao.getAllFavorites().first()
        assertEquals(1, favorites.size)
        assertEquals("Updated Title", favorites[0].title)
    }

    @Test
    fun getAllFavorites_returns_latest_first() = runTest {
        val entity1 = testEntity.copy(date = "1995-06-20", savedAt = 1000L)
        val entity2 = testEntity.copy(date = "2000-01-01", savedAt = 2000L)
        dao.insertFavorite(entity1)
        dao.insertFavorite(entity2)
        val favorites = dao.getAllFavorites().first()
        assertEquals("2000-01-01", favorites[0].date)
    }
}