package com.example.flowplayground.repo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Dog (
    @PrimaryKey val name: String,
    val cuteness: Int,
    val barkingVolume: Int
)