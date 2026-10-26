package com.example.entregaseminario.data.remote

import com.example.entregaseminario.data.remote.dto.MovieDetailsDto
import com.example.entregaseminario.data.remote.dto.MoviePage
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query as RetrofitQuery

interface TmdbService {
    @GET("movie/popular")
    suspend fun popular(
        @RetrofitQuery("page") page: Int
    ): MoviePage

    @GET("search/movie")
    suspend fun search(
        @RetrofitQuery("query") query: String,
        @RetrofitQuery("page") page: Int
    ): MoviePage

    @GET("trending/movie/week")
    suspend fun trending(): MoviePage

    @GET("movie/{id}")
    suspend fun details(
        @Path("id") id: Int
    ): MovieDetailsDto

    @GET("movie/{id}/similar")
    suspend fun similar(
        @Path("id") id: Int,
        @RetrofitQuery("page") page: Int = 1
    ): MoviePage

    @GET("movie/{id}/recommendations")
    suspend fun recommendations(
        @Path("id") id: Int,
        @RetrofitQuery("page") page: Int = 1
    ): MoviePage
}
