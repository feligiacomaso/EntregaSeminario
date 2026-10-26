package com.example.entregaseminario.data.remote.dto

data class MoviePage(
    val page: Int,
    val results: List<MovieDto>,
    val total_pages: Int
)
