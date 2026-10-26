package com.example.entregaseminario

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.entregaseminario.ui.navigation.FilmWishNavigation
import com.example.entregaseminario.ui.theme.FilmWishTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FilmWishTheme {
                FilmWishNavigation()
            }
        }
    }
}
