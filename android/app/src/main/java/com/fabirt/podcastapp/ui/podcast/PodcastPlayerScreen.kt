package com.fabirt.podcastapp.ui.podcast

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.fabirt.podcastapp.domain.model.Episode
import com.fabirt.podcastapp.ui.common.IconButton
import com.fabirt.podcastapp.ui.common.ViewModelProvider
import com.google.accompanist.insets.systemBarsPadding
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PodcastPlayerScreen(backDispatcher: OnBackPressedDispatcher) {
    val podcastPlayer = ViewModelProvider.podcastPlayer
    val episode = podcastPlayer.currentPlayingEpisode.value

    AnimatedVisibility(
        visible = episode != null && podcastPlayer.showPlayerFullScreen,
        enter = slideInVertically(
            initialOffsetY = { it }
        ),
        exit = slideOutVertically(
            targetOffsetY = { it }
        )
    ) {
        if (episode != null) {
            Content(episode, backDispatcher)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Content(episode: Episode, backDispatcher: OnBackPressedDispatcher) {
    val podcastPlayer = ViewModelProvider.podcastPlayer
    val swipeableState = rememberSwipeableState(0)
    val endAnchor = LocalConfiguration.current.screenHeightDp * LocalDensity.current.density
    val anchors = mapOf(
        0f to 0,
        endAnchor to 1
    )

    val backCallback = remember {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                podcastPlayer.showPlayerFullScreen = false
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.54f) },
                orientation = Orientation.Vertical
            )
    ) {
        if (swipeableState.currentValue >= 1) {
            LaunchedEffect("key") {
                podcastPlayer.showPlayerFullScreen = false
            }
        }
        Box(
            modifier = Modifier
                .offset { IntOffset(0, swipeableState.offset.value.roundToInt()) }
                .background(MaterialTheme.colors.background)
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            Surface(
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    IconButton(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = "close"
                    ) {
                        podcastPlayer.showPlayerFullScreen = false
                    }

                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        PodcastImage(
                            url = episode.image,
                            modifier = Modifier.padding(vertical = 32.dp)
                        )
                        Text(
                            episode.titleOriginal,
                            style = MaterialTheme.typography.h5,
                            color = MaterialTheme.colors.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )

                        Text(
                            episode.podcast.titleOriginal,
                            style = MaterialTheme.typography.subtitle1,
                            color = MaterialTheme.colors.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.graphicsLayer {
                                alpha = 0.60f
                            }
                        )
                    }
                }
            }
        }
    }

    DisposableEffect(backDispatcher) {
        backDispatcher.addCallback(backCallback)

        onDispose {
            backCallback.remove()
            podcastPlayer.showPlayerFullScreen = false
        }
    }
}