package com.example.entregaseminario.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class TmdbAuthInterceptor(private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        if (token.isBlank()) {
            throw IOException("TMDB_READ_ACCESS_TOKEN is missing")
        }

        return chain.proceed(
            chain.request()
                .newBuilder()
                .header("Authorization", "Bearer " + token)
                .build()
        )
    }
}
