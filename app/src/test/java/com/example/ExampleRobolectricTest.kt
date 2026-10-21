package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.model.GameItem
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read app_name string matches After Game`() {
        val appName = context.getString(R.string.app_name)
        assertEquals("After Game", appName)
    }

    @Test
    fun `test game insertion and review update in database`() = runBlocking {
        val gameDao = database.gameDao()

        val newGame = GameItem(
            id = 100L,
            title = "Elden Ring",
            titleAr = "ألدن رينج",
            developer = "FromSoftware",
            releaseYear = 2022,
            genres = "Action RPG, Soulslike",
            platforms = "PC, PS5",
            userRating = 5.0f,
            status = "COMPLETED",
            hoursPlayed = 120.0f,
            userReview = "Masterpiece RPG",
            reviewTitle = "Best Soulslike ever"
        )

        gameDao.insertGame(newGame)

        val retrieved = gameDao.getGameById(100L).first()
        assertNotNull(retrieved)
        assertEquals("Elden Ring", retrieved?.title)
        assertEquals(5.0f, retrieved?.userRating)
        assertEquals("COMPLETED", retrieved?.status)
        assertTrue(retrieved?.isLogged == true)

        // Update rating and hours
        val updatedGame = retrieved!!.copy(
            userRating = 4.5f,
            hoursPlayed = 150.0f
        )
        gameDao.updateGame(updatedGame)

        val updatedRetrieved = gameDao.getGameById(100L).first()
        assertEquals(4.5f, updatedRetrieved?.userRating)
        assertEquals(150.0f, updatedRetrieved?.hoursPlayed)
    }
}
