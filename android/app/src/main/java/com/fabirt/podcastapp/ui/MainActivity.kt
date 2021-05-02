package com.fabirt.podcastapp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.fabirt.podcastapp.R
import com.fabirt.podcastapp.ui.home.HomeScreen
import com.fabirt.podcastapp.ui.navigation.Destination
import com.fabirt.podcastapp.ui.navigation.Navigator
import com.fabirt.podcastapp.ui.navigation.ProvideNavHostController
import com.fabirt.podcastapp.ui.podcast.PodcastDetailScreen
import com.fabirt.podcastapp.ui.theme.PodcastAppTheme
import com.fabirt.podcastapp.ui.viewmodel.PodcastSearchViewModel
import com.fabirt.podcastapp.ui.welcome.WelcomeScreen
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_PodcastApp)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            PodcastApp()
        }
    }
}

@ExperimentalFoundationApi
@ExperimentalAnimationApi
@Composable
fun PodcastApp() {
    val podcastSearchViewModel: PodcastSearchViewModel = viewModel()
    PodcastAppTheme {
        ProvideWindowInsets {
            ProvideNavHostController {
                NavHost(Navigator.current, startDestination = Destination.welcome) {
                    composable(Destination.welcome) { WelcomeScreen() }

                    composable(Destination.home) {
                        HomeScreen(podcastSearchViewModel)
                    }

                    composable(Destination.podcast) { backStackEntry ->
                        PodcastDetailScreen(
                            podcastId = backStackEntry.arguments?.getString("id")!!,
                            podcastSearchViewModel = podcastSearchViewModel
                        )
                    }
                }
            }
        }
    }
}
