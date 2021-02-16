package com.example.flowplayground.repo

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
abstract class DogDao {
    // TODO Show how to create a wrapper method so app can use it as a Flow
    @Query("""SELECT * FROM Dog""")
    abstract fun getAllDogs(): List<Dog>

    @Query("""SELECT * FROM Dog""")
    abstract fun getAllDogsFlow(): Flow<List<Dog>>

    @Query("""SELECT * FROM Dog WHERE name = :name""")
    protected abstract fun _getDog(name: String): Flow<Dog>
    fun getDog(name: String) =
        _getDog(name).distinctUntilChanged()

    @Query("""SELECT * FROM Dog WHERE name LIKE '%' || :name || '%'""")
    protected abstract fun _getDogs(name: String): Flow<List<Dog>>
    fun getDogs(name: String) =
        _getDogs(name).distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addDog(dog: Dog)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun addDogs(dogs: List<Dog>)
}