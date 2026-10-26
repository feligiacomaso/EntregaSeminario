package com.example.entregaseminario.ui.details

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.entregaseminario.R
import com.example.entregaseminario.data.remote.dto.MovieDetailsDto
import com.example.entregaseminario.domain.model.Movie
import com.example.entregaseminario.ui.components.ErrorView
import com.example.entregaseminario.ui.components.LoadingView
import com.example.entregaseminario.ui.components.Poster
import com.example.entregaseminario.ui.state.LoadState
import java.util.Locale

@Composable
fun DetailScreen(
    id: Int,
    vm: DetailViewModel,
    onBack: () -> Unit,
    onMovie: (Int) -> Unit
) {
    val context = LocalContext.current
    val shareError = stringResource(R.string.share_error)
    val homepageError = stringResource(R.string.homepage_error)
    val state by vm.details.collectAsStateWithLifecycle()
    val similar by vm.similar.collectAsStateWithLifecycle()
    val recommendations by vm.recommendations.collectAsStateWithLifecycle()
    val saved by vm.saved.collectAsStateWithLifecycle()

    LaunchedEffect(id) {
        vm.load(id)
    }

    Column(Modifier.fillMaxSize()) {
        TextButton(
            onClick = onBack,
            modifier = Modifier.padding(start = 8.dp, top = 6.dp)
        ) {
            Text("‹  " + stringResource(R.string.popular))
        }
        when (val result = state) {
            LoadState.Loading -> LoadingView()
            is LoadState.Failure -> ErrorView(result.message, vm::retry)
            is LoadState.Ready<*> -> {
                val details = result.value as MovieDetailsDto
                val movie = details.toMovie()

                LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
                    item {
                        Poster(
                            details.backdrop_path ?: details.poster_path,
                            Modifier.fillMaxWidth().height(250.dp)
                        )
                        Column(
                            Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Text(
                                movie.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (movie.rating > 0.0) {
                                Text(
                                    stringResource(R.string.rating) + "  ★ " +
                                        stringResource(R.string.rating_value , movie.rating)
,
                                    color = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            Text(
                                if (details.genres.isNullOrEmpty()) {
                                    ""
                                } else {
                                    stringResource(R.string.genres) + ": " +
                                        details.genres.joinToString { it.name }
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            )
                            Text(
                                movie.releaseDate,
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                if (movie.overview.isBlank()) {
                                    stringResource(R.string.overview_missing)
                                } else {
                                    movie.overview
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            val shareLabel = stringResource(R.string.share)
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 12.dp)
                            ) {
                                Button(onClick = vm::toggle) {
                                    Text(
                                        stringResource(
                                            if (saved) {
                                                R.string.remove_watchlist
                                            } else {
                                                R.string.add_watchlist
                                            }
                                        )
                                    )
                                }
                                OutlinedButton(
                                    onClick = {
                                        val share = Intent(Intent.ACTION_SEND)
                                            .setType("text/plain")
                                            .putExtra(
                                                Intent.EXTRA_TEXT,
                                                movie.title + "\n\n" + movie.overview
                                            )
                                        runCatching {
                                            context.startActivity(
                                                Intent.createChooser(share, shareLabel)
                                            )
                                        }.onFailure {
                                            Toast.makeText(
                                                context,
                                                shareError,
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                ) {
                                    Text(stringResource(R.string.share))
                                }
                            }
                            val homepageUri = runCatching {
                                Uri.parse(details.homepage.orEmpty())
                            }.getOrNull()
                            homepageUri
                                ?.takeIf {
                                    (it.scheme == "https" || it.scheme == "http") &&
                                        !it.host.isNullOrBlank()
                                }
                                ?.let { uri ->
                                    OutlinedButton(
                                        onClick = {
                                            runCatching {
                                                context.startActivity(
                                                    Intent(Intent.ACTION_VIEW, uri)
                                                )
                                            }.onFailure {
                                                Toast.makeText(
                                                    context,
                                                    homepageError,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    ) {
                                        Text(stringResource(R.string.homepage))
                                    }
                                }
                        }
                    }
                    item {
                        RelatedSection(
                            stringResource(R.string.similar),
                            similar,
                            onMovie
                        )
                    }
                    item {
                        RelatedSection(
                            stringResource(R.string.recommendations),
                            recommendations,
                            onMovie
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RelatedSection(
    title: String,
    state: LoadState<List<Movie>>,
    onMovie: (Int) -> Unit
) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
        )
        when (state) {
            LoadState.Loading -> LinearProgressIndicator(
                Modifier.fillMaxWidth().padding(horizontal = 18.dp)
            )
            is LoadState.Failure -> Text(
                state.message,
                modifier = Modifier.padding(horizontal = 18.dp),
                style = MaterialTheme.typography.bodySmall
            )
            is LoadState.Ready<*> -> {
                val movies = (state.value as? List<*>)
                    ?.filterIsInstance<Movie>()
                    .orEmpty()
                if (movies.isEmpty()) {
                    Text("—", Modifier.padding(horizontal = 18.dp))
                } else {
                    Row {
                        movies.take(10).forEach { movie ->
                            Column(
                                Modifier
                                    .width(140.dp)
                                    .clickable { onMovie(movie.id) }
                                    .padding(8.dp)
                            ) {
                                Poster(
                                    movie.posterPath,
                                    Modifier.fillMaxWidth().height(180.dp)
                                )
                                Text(
                                    movie.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
