package com.example.flowplayground.repo

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AnimalDaoTest {
    private lateinit var animalDao: AnimalDao
    private lateinit var db: AppDatabase

    private val testDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(testDispatcher)

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .setTransactionExecutor(testDispatcher.asExecutor())
            .setQueryExecutor(testDispatcher.asExecutor())
            .build()
        animalDao = db.animalDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    /*
      How to test with coroutines and flows
      https://medium.com/@eyalg/testing-androidx-room-kotlin-coroutines-2d1faa3e674f
     */

    @Test
    @Throws(Exception::class)
    fun testAdd() = testScope.runBlockingTest {
        val animal = Animal("test", 0, 0)
        animalDao.add(animal)

        val expected = listOf(animal)
        val actual = animalDao.getAll()

        assertEquals(expected, actual)
    }

    @Test
    @Throws(Exception::class)
    fun testSearch_CaseInsensitive() = testScope.runBlockingTest {
        val animals = listOf(
            Animal("test", 0, 0),
            Animal("Test", 0, 0),
            Animal("tEst", 0, 0),
            Animal("teSt", 0, 0),
            Animal("tesT", 0, 0),
            Animal("TEst", 0, 0),
            Animal("TeSt", 0, 0),
            Animal("TesT", 0, 0),
            Animal("tESt", 0, 0),
            Animal("tEsT", 0, 0),
            Animal("teST", 0, 0),
            Animal("TeST", 0, 0),
            Animal("tEST", 0, 0),
            Animal("TEST", 0, 0),
            Animal("drop", 0, 0),
        )
        animalDao.add(animals)

        val searchTerm = "test"
        val expected = animals.filter { it.name.contains(searchTerm, ignoreCase = true) }
        val actual = async {
            animalDao.search(searchTerm)
                .take(1)
                .toList()
                .flatten()
        }

        assertEquals(expected, actual.await())
    }

    @Test
    @Throws(Exception::class)
    fun testSearch_PartialMatches() = testScope.runBlockingTest {
        val animals = listOf(
            Animal("test", 0, 0),
            Animal("testing", 0, 0),
            Animal("tester", 0, 0),
            Animal("cutest", 0, 0),
            Animal("drop", 0, 0),
        )
        animalDao.add(animals)

        val searchTerm = "test"
        val expected = animals.filter { it.name.contains(searchTerm, ignoreCase = true) }
        val actual = async {
            animalDao.search(searchTerm)
                .take(1)
                .toList()
                .flatten()
        }

        assertEquals(expected, actual.await())
    }
}