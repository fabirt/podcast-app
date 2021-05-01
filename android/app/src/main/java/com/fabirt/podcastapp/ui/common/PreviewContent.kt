package com.fabirt.podcastapp.ui.common

import androidx.compose.runtime.Composable
import com.fabirt.podcastapp.ui.navigation.ProvideNavHostController
import com.fabirt.podcastapp.ui.theme.PodcastAppTheme
import com.google.accompanist.insets.ProvideWindowInsets

@Composable
fun PreviewContent(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    PodcastAppTheme(darkTheme = darkTheme) {
        ProvideWindowInsets {
            ProvideNavHostController {
                content()
            }
        }
    }
}