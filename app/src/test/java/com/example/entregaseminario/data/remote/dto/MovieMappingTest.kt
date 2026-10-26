package com.example.entregaseminario.data.remote.dto

import org.junit.Assert.assertEquals
import org.junit.Test

class MovieMappingTest {
    @Test
    fun mapsMovieFieldsAndFallbackTitle() {
        val movie = MovieDto(
            42,
            null,
            "Título",
            "Resumen",
            "/poster.jpg",
            null,
            7.5,
            "2025-01-02"
        ).toMovie()

        assertEquals(42, movie.id)
        assertEquals("Título", movie.title)
        assertEquals("Resumen", movie.overview)
        assertEquals("/poster.jpg", movie.posterPath)
        assertEquals(7.5, movie.rating, 0.0)
    }

    @Test
    fun mapsMissingOptionalValuesSafely() {
        val movie = MovieDto(
            1,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        ).toMovie()

        assertEquals("", movie.title)
        assertEquals("", movie.overview)
        assertEquals(0.0, movie.rating, 0.0)
    }
}
