package com.example.entregaseminario.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.entregaseminario.data.local.entity.SavedMovie
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {
    @Query("SELECT * FROM watchlist ORDER BY title COLLATE NOCASE")
    fun observeAll(): Flow<List<SavedMovie>>

    @Query("SELECT EXISTS(SELECT 1 FROM watchlist WHERE id = :id)")
    fun observeSaved(id: Int): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: SavedMovie)

    @Query("DELETE FROM watchlist WHERE id = :id")
    suspend fun remove(id: Int)
}
