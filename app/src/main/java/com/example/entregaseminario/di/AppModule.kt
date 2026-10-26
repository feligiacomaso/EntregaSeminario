@file:Suppress("unused")

package com.example.entregaseminario.di

import android.app.Application
import androidx.room.Room
import com.example.entregaseminario.BuildConfig
import com.example.entregaseminario.data.local.dao.WatchlistDao
import com.example.entregaseminario.data.local.database.FilmWishDatabase
import com.example.entregaseminario.data.remote.LocaleInterceptor
import com.example.entregaseminario.data.remote.TmdbAuthInterceptor
import com.example.entregaseminario.data.remote.TmdbService
import com.example.entregaseminario.data.repository.MovieRepository
import com.example.entregaseminario.data.repository.TmdbRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun httpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(LocaleInterceptor())
        .addInterceptor(TmdbAuthInterceptor(BuildConfig.TMDB_READ_ACCESS_TOKEN))
        .build()

    @Provides
    @Singleton
    fun tmdbService(client: OkHttpClient): TmdbService = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TmdbService::class.java)

    @Provides
    @Singleton
    fun database(app: Application): FilmWishDatabase = Room.databaseBuilder(
        app,
        FilmWishDatabase::class.java,
        "filmwish.db"
    ).build()

    @Provides
    fun watchlistDao(db: FilmWishDatabase): WatchlistDao = db.watchlistDao()

    @Provides
    @Singleton
    fun repository(service: TmdbService, dao: WatchlistDao): MovieRepository =
        TmdbRepository(service, dao)
}
