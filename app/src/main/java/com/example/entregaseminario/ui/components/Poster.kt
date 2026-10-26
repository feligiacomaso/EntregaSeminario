package com.example.entregaseminario.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage

@Composable
fun Poster(path: String?, modifier: Modifier = Modifier) {
    val url = if (path.isNullOrBlank()) {
        null
    } else {
        "https://image.tmdb.org/t/p/w500" + path
    }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        if (url == null) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    "🎬",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        } else {
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
