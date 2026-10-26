package com.example.entregaseminario.ui.home

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.LoadState as PagingLoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.entregaseminario.R
import com.example.entregaseminario.domain.model.Movie
import com.example.entregaseminario.ui.components.EmptyView
import com.example.entregaseminario.ui.components.ErrorView
import com.example.entregaseminario.ui.components.LoadingView
import com.example.entregaseminario.ui.components.MovieCard
import com.example.entregaseminario.ui.navigation.Section
import com.example.entregaseminario.ui.state.LoadState
import com.example.entregaseminario.ui.state.errorText
import java.io.IOException

@Composable
fun HomeScreen(
    section: Section,
    nav: NavHostController,
    vm: HomeViewModel = hiltViewModel()
) {
    val query by vm.query.collectAsStateWithLifecycle()
    val settledQuery by vm.settledQuery.collectAsStateWithLifecycle()
    val saved by vm.watchlist.collectAsStateWithLifecycle()
    val trending by vm.trending.collectAsStateWithLifecycle()
    val popular = vm.popular.collectAsLazyPagingItems()
    val search = vm.searchResults.collectAsLazyPagingItems()
    val landscape = LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchInteractionSource = remember { MutableInteractionSource() }
    val searchFieldFocused by searchInteractionSource.collectIsFocusedAsState()

    Column(Modifier.fillMaxSize()) {
        Text(
            "FilmWish",
            Modifier.padding(start = 20.dp, top = 16.dp, bottom = 8.dp),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        if (section == Section.Popular) {
            OutlinedTextField(
                value = query,
                onValueChange = vm::setQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = {
                    Text(stringResource(R.string.search_hint))
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_search),
                        contentDescription = stringResource(R.string.search_hint)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                interactionSource = searchInteractionSource,
                trailingIcon = {
                    if (query.isNotBlank() && searchFieldFocused) {
                        IconButton(
                            onClick = {
                                vm.setQuery("")
                                keyboardController?.hide()
                                focusManager.clearFocus()
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_close),
                                contentDescription = stringResource(R.string.clear_search_action)
                            )
                        }
                    }
                }
            )
            Spacer(Modifier.height(8.dp))
        }
        when (section) {
            Section.Popular -> if (query.isNotBlank() && query.trim() != settledQuery) {
                LoadingView()
            } else {
                MoviePagingList(if (query.isBlank()) popular else search, landscape, nav, query.isNotBlank())
            }
            Section.Trending -> when (val state = trending) {
                LoadState.Loading -> LoadingView()
                is LoadState.Failure -> ErrorView(state.message, vm::loadTrending)
                is LoadState.Ready<*> -> MovieList(
                    (state.value as? List<*>)?.filterIsInstance<Movie>().orEmpty(),
                    landscape,
                    nav
                )
            }
            Section.Watchlist -> if (saved.isEmpty()) {
                EmptyView(stringResource(R.string.empty_watchlist))
            } else {
                MovieList(
                    saved.map { Movie(it.id, it.title, "", it.posterPath, it.rating, it.releaseDate) },
                    landscape,
                    nav
                )
            }
        }
    }
}

@Composable
private fun MoviePagingList(
    items: LazyPagingItems<Movie>,
    landscape: Boolean,
    nav: NavHostController,
    searching: Boolean
) {
    when {
        items.loadState.refresh is PagingLoadState.Error -> ErrorView(
            errorText((items.loadState.refresh as PagingLoadState.Error).error as? Exception ?: IOException()),
            items::retry
        )
        items.loadState.refresh is PagingLoadState.Loading && items.itemCount == 0 -> LoadingView()
        items.loadState.refresh is PagingLoadState.NotLoading && items.itemCount == 0 ->
            EmptyView(stringResource(if (searching) R.string.empty_search else R.string.empty_list))
        else -> {
            if (landscape) {
                LazyVerticalGrid(
                    GridCells.Fixed(2),
                    Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items.itemCount) { i ->
                        items[i]?.let { movie ->
                            MovieCard(movie, true) {
                                nav.navigate("detail/" + movie.id)
                            }
                        }
                    }
                    if (items.loadState.append is PagingLoadState.Loading) {
                        item { CircularProgressIndicator(Modifier.padding(20.dp)) }
                    }
                    if (items.loadState.append is PagingLoadState.Error) {
                        item { TextButton(onClick = items::retry) { Text(stringResource(R.string.retry)) } }
                    }
                }
            } else {
                LazyColumn(
                    Modifier.fillMaxSize(),
                    state = rememberLazyListState(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items.itemCount) { i ->
                        items[i]?.let { movie ->
                            MovieCard(movie, false) {
                                nav.navigate("detail/" + movie.id)
                            }
                        }
                    }
                    if (items.loadState.append is PagingLoadState.Loading) {
                        item {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(Modifier.padding(18.dp))
                            }
                        }
                    }
                    if (items.loadState.append is PagingLoadState.Error) {
                        item { TextButton(onClick = items::retry) { Text(stringResource(R.string.retry)) } }
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieList(movies: List<Movie>, landscape: Boolean, nav: NavHostController) {
    if (movies.isEmpty()) {
        EmptyView(stringResource(R.string.empty_list))
        return
    }
    if (landscape) {
        LazyVerticalGrid(
            GridCells.Fixed(2),
            Modifier.fillMaxSize().padding(horizontal = 12.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(movies, key = { it.id }) { movie -> MovieCard(movie, true) { nav.navigate("detail/${movie.id}") } }
        }
    } else {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(movies, key = { it.id }) { movie -> MovieCard(movie, false) { nav.navigate("detail/${movie.id}") } }
        }
    }
}




