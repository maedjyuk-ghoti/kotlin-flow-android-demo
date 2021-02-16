package com.example.flowplayground.repo

import android.app.Application
import androidx.room.Room

object DatabaseHolder {
    private lateinit var db: AppDatabase
    
    fun getDatabase(applicationContext: Application): AppDatabase {
        if (::db.isInitialized.not()) {
            db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "database-name"
            ).build()
        }
        return db
    }
}