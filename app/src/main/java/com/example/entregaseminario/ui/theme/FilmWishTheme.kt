package com.example.entregaseminario.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun FilmWishTheme(content: @Composable () -> Unit) {
    val dark = isSystemInDarkTheme()
    val colors = if (dark) {
        darkColorScheme(
            primary = Color(0xFFB69CFF),
            secondary = Color(0xFF70D1C2),
            background = Color(0xFF101116),
            surface = Color(0xFF191B24)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF6842B8),
            secondary = Color(0xFF155E63)
        )
    }

    MaterialTheme(colorScheme = colors, content = content)
}
