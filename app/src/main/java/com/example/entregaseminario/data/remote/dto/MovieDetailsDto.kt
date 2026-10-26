package com.example.entregaseminario.data.remote.dto

import com.example.entregaseminario.domain.model.Movie

data class MovieDetailsDto(
    val id: Int,
    val title: String?,
    val overview: String?,
    val poster_path: String?,
    val backdrop_path: String?,
    val vote_average: Double?,
    val release_date: String?,
    val runtime: Int?,
    val genres: List<GenreDto>?,
    val homepage: String?
) {
    fun toMovie() = Movie(
        id = id,
        title = title.orEmpty(),
        overview = overview.orEmpty(),
        posterPath = poster_path,
        rating = vote_average ?: 0.0,
        releaseDate = release_date.orEmpty()
    )
}
