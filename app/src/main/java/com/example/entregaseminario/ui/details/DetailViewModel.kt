package com.example.entregaseminario.ui.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.entregaseminario.data.remote.dto.MovieDetailsDto
import com.example.entregaseminario.data.repository.MovieRepository
import com.example.entregaseminario.domain.model.Movie
import com.example.entregaseminario.ui.state.LoadState
import com.example.entregaseminario.ui.state.resultState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repo: MovieRepository
) : ViewModel() {
    private val _details = MutableStateFlow<LoadState<MovieDetailsDto>>(LoadState.Loading)
    val details = _details.asStateFlow()

    private val _similar = MutableStateFlow<LoadState<List<Movie>>>(LoadState.Loading)
    val similar = _similar.asStateFlow()

    private val _recommendations = MutableStateFlow<LoadState<List<Movie>>>(LoadState.Loading)
    val recommendations = _recommendations.asStateFlow()

    private val _saved = MutableStateFlow(false)
    val saved = _saved.asStateFlow()

    private var activeId: Int? = null

    fun load(id: Int) {
        if (activeId == id) {
            return
        }
        activeId = id

        viewModelScope.launch {
            _details.value = LoadState.Loading
            _details.value = resultState { repo.details(id) }
        }
        viewModelScope.launch {
            _similar.value = resultState { repo.similar(id) }
        }
        viewModelScope.launch {
            _recommendations.value = resultState { repo.recommendations(id) }
        }
        viewModelScope.launch {
            repo.isSaved(id).collect { _saved.value = it }
        }
    }

    fun retry() {
        activeId?.let {
            activeId = null
            load(it)
        }
    }

    fun toggle() {
        val item = (_details.value as? LoadState.Ready<MovieDetailsDto>)
            ?.value
            ?.toMovie()
            ?: return
        viewModelScope.launch {
            repo.toggle(item)
        }
    }
}
