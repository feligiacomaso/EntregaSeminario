package com.example.entregaseminario.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.entregaseminario.data.local.dao.WatchlistDao
import com.example.entregaseminario.data.local.entity.SavedMovie
import com.example.entregaseminario.data.remote.TmdbService
import com.example.entregaseminario.domain.model.Movie
import com.example.entregaseminario.paging.TmdbPagingSource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TmdbRepository @Inject constructor(
    private val service: TmdbService,
    private val dao: WatchlistDao
) : MovieRepository {
    override fun popular() = Pager(
        PagingConfig(pageSize = 20, enablePlaceholders = false)
    ) {
        TmdbPagingSource(service, null)
    }.flow

    @OptIn(FlowPreview::class)
    override fun search(query: String) = Pager(
        PagingConfig(pageSize = 20, enablePlaceholders = false)
    ) {
        TmdbPagingSource(service, query)
    }.flow

    override suspend fun trending() = service.trending()
        .results
        .map { it.toMovie() }
        .distinctBy { it.id }

    override suspend fun details(id: Int) = service.details(id)

    override suspend fun similar(id: Int) = service.similar(id)
        .results
        .map { it.toMovie() }
        .distinctBy { it.id }

    override suspend fun recommendations(id: Int) = service.recommendations(id)
        .results
        .map { it.toMovie() }
        .distinctBy { it.id }

    override fun watchlist() = dao.observeAll()

    override fun isSaved(id: Int) = dao.observeSaved(id)

    override suspend fun toggle(movie: Movie) {
        if (dao.observeSaved(movie.id).first()) {
            dao.remove(movie.id)
        } else {
            dao.insert(
                SavedMovie(
                    movie.id,
                    movie.title,
                    movie.posterPath,
                    movie.rating,
                    movie.releaseDate
                )
            )
        }
    }
}
