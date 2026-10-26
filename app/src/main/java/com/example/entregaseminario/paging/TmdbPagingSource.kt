package com.example.entregaseminario.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.entregaseminario.data.remote.TmdbService
import com.example.entregaseminario.domain.model.Movie

class TmdbPagingSource(
    private val service: TmdbService,
    private val query: String?
) : PagingSource<Int, Movie>() {
    private val seenIds = mutableSetOf<Int>()

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: 1
        return try {
            val result = if (query.isNullOrBlank()) {
                service.popular(page)
            } else {
                service.search(query, page)
            }

            if (page == 1) {
                seenIds.clear()
            }

            val uniqueMovies = result.results
                .map { it.toMovie() }
                .filter { seenIds.add(it.id) }

            LoadResult.Page(
                uniqueMovies,
                if (page == 1) null else page - 1,
                if (page >= result.total_pages) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}



