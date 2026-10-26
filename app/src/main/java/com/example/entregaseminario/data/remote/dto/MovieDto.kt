package com.example.entregaseminario.data.remote.dto

import com.example.entregaseminario.domain.model.Movie

data class MovieDto(
    val id: Int,
    val title: String?,
    val name: String?,
    val overview: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val vote_average: Double?,
    val release_date: String?
) {
    fun toMovie() = Movie(
        id = id,
        title = title ?: name ?: "",
        overview = overview.orEmpty(),
        posterPath = poster_path,
        rating = vote_average ?: 0.0,
        releaseDate = release_date.orEmpty()
    )
}
