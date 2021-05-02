package com.fabirt.podcastapp.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fabirt.podcastapp.ui.viewmodel.PodcastSearchViewModel

object ViewModelProvider {
    val podcastSearch: PodcastSearchViewModel
        @Composable
        get() = LocalPodcastSearchViewModel.current
}

@Composable
fun ProvideMultiViewModel(content: @Composable () -> Unit) {
    val viewModel: PodcastSearchViewModel = viewModel()

    CompositionLocalProvider(
        LocalPodcastSearchViewModel provides viewModel,
    ) {
        content()
    }
}

private val LocalPodcastSearchViewModel = staticCompositionLocalOf<PodcastSearchViewModel> {
    error("No PodcastSearchViewModel provided")
}