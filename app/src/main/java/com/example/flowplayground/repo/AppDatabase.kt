package com.example.flowplayground.repo

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Dog::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dogDao(): DogDao
}