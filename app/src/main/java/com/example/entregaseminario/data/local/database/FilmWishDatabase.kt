package com.example.entregaseminario.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.entregaseminario.data.local.dao.WatchlistDao
import com.example.entregaseminario.data.local.entity.SavedMovie

@Database(entities = [SavedMovie::class], version = 1, exportSchema = true)
abstract class FilmWishDatabase : RoomDatabase() {
    abstract fun watchlistDao(): WatchlistDao
}
