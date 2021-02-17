package com.example.flowplayground.repo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
abstract class AnimalDao {
    @Query("""SELECT * FROM Animal""")
    abstract suspend fun getAll(): List<Animal>

    @Query("""SELECT * FROM Animal""")
    abstract fun getAllFlow(): Flow<List<Animal>>

    @Query("""SELECT * FROM Animal WHERE name = :name""")
    protected abstract fun _get(name: String): Flow<Animal>
    fun get(name: String) =
        _get(name).distinctUntilChanged()

    @Query("""SELECT * FROM Animal WHERE name LIKE '%' || :name || '%'""")
    protected abstract fun _search(name: String): Flow<List<Animal>>
    fun search(name: String) =
        _search(name).distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun add(animal: Animal)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun add(animals: List<Animal>)
}