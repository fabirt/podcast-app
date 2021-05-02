package com.fabirt.podcastapp.ui.podcast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.fabirt.podcastapp.R
import com.fabirt.podcastapp.domain.model.Episode
import com.fabirt.podcastapp.ui.common.IconButton
import com.fabirt.podcastapp.ui.common.ViewModelProvider
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.insets.navigationBarsPadding
import kotlin.math.roundToInt

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PodcastBottomView(
    modifier: Modifier = Modifier
) {
    val episode = ViewModelProvider.podcastPlayer.currentPlayingEpisode.value

    AnimatedVisibility(
        visible = episode != null,
        modifier = modifier
    ) {
        if (episode != null) {
            PodcastBottomViewContent(episode)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PodcastBottomViewContent(episode: Episode) {
    val swipeableState = rememberSwipeableState(0)
    val podcastPlayer = ViewModelProvider.podcastPlayer

    val endAnchor = LocalConfiguration.current.screenWidthDp * LocalDensity.current.density
    val anchors = mapOf(
        0f to 0,
        endAnchor to 1
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.54f) },
                orientation = Orientation.Horizontal
            )
    ) {
        if (swipeableState.currentValue >= 1) {
            LaunchedEffect("key") {
                podcastPlayer.stopPlayback()
            }
        }

        Box(
            modifier = Modifier
                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                .background(if (isSystemInDarkTheme()) Color(0xFF343434) else Color(0xFFF1F1F1))
                .navigationBarsPadding()
                .height(64.dp)
                .fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                ) {
                    Image(
                        painter = rememberCoilPainter(episode.thumbnail),
                        contentDescription = stringResource(R.string.podcast_thumbnail),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(8.dp),
                ) {
                    Text(
                        episode.titleOriginal,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Text(
                        episode.podcast.titleOriginal,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.graphicsLayer {
                            alpha = 0.60f
                        }
                    )
                }

                Box(
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    IconButton(
                        imageVector = Icons.Rounded.PlayArrow,
                        contentDescription = stringResource(R.string.play),
                        padding = 6.dp,
                        tint = MaterialTheme.colors.onBackground,
                        modifier = Modifier.size(40.dp)
                    ) {

                    }
                }
            }
        }
    }
}