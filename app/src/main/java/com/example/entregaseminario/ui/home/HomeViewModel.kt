package com.example.entregaseminario.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.entregaseminario.data.repository.MovieRepository
import com.example.entregaseminario.domain.model.Movie
import com.example.entregaseminario.ui.state.LoadState
import com.example.entregaseminario.ui.state.resultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: MovieRepository
) : ViewModel() {
    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    private val _settledQuery = MutableStateFlow("")
    val settledQuery = _settledQuery.asStateFlow()

    val searchResults = query
        .map { it.trim() }
        .debounce(350)
        .distinctUntilChanged()
        .onEach { _settledQuery.value = it }
        .flatMapLatest { q ->
            if (q.isBlank()) {
                flowOf(PagingData.empty())
            } else {
                repo.search(q)
            }
        }
        .cachedIn(viewModelScope)

    val popular = repo.popular().cachedIn(viewModelScope)
    val watchlist = repo.watchlist().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    private val _trending = MutableStateFlow<LoadState<List<Movie>>>(LoadState.Loading)
    val trending = _trending.asStateFlow()

    init {
        loadTrending()
    }

    fun setQuery(value: String) {
        _query.value = value
    }

    fun loadTrending() {
        viewModelScope.launch {
            _trending.value = LoadState.Loading
            _trending.value = resultState { repo.trending() }
        }
    }
}

