package com.example.entregaseminario.data.remote

import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

class LocaleInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val locale = Locale.getDefault()
        val language = locale.toLanguageTag()
        val url = request.url
            .newBuilder()
            .setQueryParameter("language", language)
            .build()

        return chain.proceed(request.newBuilder().url(url).build())
    }
}
