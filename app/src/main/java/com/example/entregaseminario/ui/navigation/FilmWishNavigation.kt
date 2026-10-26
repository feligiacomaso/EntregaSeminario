package com.example.entregaseminario.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.entregaseminario.R
import com.example.entregaseminario.ui.details.DetailScreen
import com.example.entregaseminario.ui.details.DetailViewModel
import com.example.entregaseminario.ui.home.HomeScreen


enum class Section {
    Popular,
    Trending,
    Watchlist
}

@Composable
fun FilmWishNavigation() {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route ?: "home"

    Scaffold(
        bottomBar = {
            if (!current.startsWith("detail")) {
                NavigationBar {
                    val labels = listOf(
                        stringResource(R.string.popular),
                        stringResource(R.string.trending),
                        stringResource(R.string.watchlist)
                    )
                    Section.entries.forEachIndexed { index, section ->
                        val icon = when (section) {
                            Section.Popular -> R.drawable.ic_star
                            Section.Trending -> R.drawable.ic_electric_bolt
                            Section.Watchlist -> R.drawable.ic_favorite
                        }

                        NavigationBarItem(
                            selected = current == section.name.lowercase(),
                            onClick = {
                                nav.navigate(section.name.lowercase()) {
                                    popUpTo("popular") {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    painter = painterResource(icon),
                                    contentDescription = null
                                )
                            },
                            label = { Text(labels[index]) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = "popular",
            modifier = Modifier.padding(padding)
        ) {
            composable("popular") {
                HomeScreen(Section.Popular, nav)
            }
            composable("trending") {
                HomeScreen(Section.Trending, nav)
            }
            composable("watchlist") {
                HomeScreen(Section.Watchlist, nav)
            }
            composable(
                "detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { entry ->
                val vm: DetailViewModel = hiltViewModel()
                val id = entry.arguments?.getInt("id") ?: 0
                DetailScreen(
                    id = id,
                    vm = vm,
                    onBack = { nav.popBackStack() },
                    onMovie = { nav.navigate("detail/" + it) }
                )
            }
        }
    }
}
