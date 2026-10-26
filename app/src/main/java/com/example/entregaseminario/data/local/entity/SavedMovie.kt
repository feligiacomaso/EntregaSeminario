package com.example.entregaseminario.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class SavedMovie(
    @PrimaryKey
    val id: Int,
    val title: String,
    val posterPath: String?,
    val rating: Double,
    val releaseDate: String
)
