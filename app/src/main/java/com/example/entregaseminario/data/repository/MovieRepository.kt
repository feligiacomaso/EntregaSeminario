package com.example.entregaseminario.data.repository

import androidx.paging.PagingData
import com.example.entregaseminario.data.local.entity.SavedMovie
import com.example.entregaseminario.data.remote.dto.MovieDetailsDto
import com.example.entregaseminario.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun popular(): Flow<PagingData<Movie>>

    fun search(query: String): Flow<PagingData<Movie>>

    suspend fun trending(): List<Movie>

    suspend fun details(id: Int): MovieDetailsDto

    suspend fun similar(id: Int): List<Movie>

    suspend fun recommendations(id: Int): List<Movie>

    fun watchlist(): Flow<List<SavedMovie>>

    fun isSaved(id: Int): Flow<Boolean>

    suspend fun toggle(movie: Movie)
}
