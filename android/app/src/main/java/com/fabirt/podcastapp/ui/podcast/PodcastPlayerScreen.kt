package com.fabirt.podcastapp.ui.podcast

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.ProgressIndicatorDefaults.IndicatorBackgroundOpacity
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.fabirt.podcastapp.R
import com.fabirt.podcastapp.domain.model.Episode
import com.fabirt.podcastapp.domain.model.Podcast
import com.fabirt.podcastapp.ui.common.EmphasisText
import com.fabirt.podcastapp.ui.common.IconButton
import com.fabirt.podcastapp.ui.common.PreviewContent
import com.fabirt.podcastapp.ui.common.ViewModelProvider
import com.google.accompanist.coil.rememberCoilPainter
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
            PodcastPlayerBody(episode, backDispatcher)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun PodcastPlayerBody(episode: Episode, backDispatcher: OnBackPressedDispatcher) {
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

    val backgroundColor = MaterialTheme.colors.background
    var gradientColor by remember {
        mutableStateOf(backgroundColor)
    }

    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(episode.image)
        .target {
            podcastPlayer.calculateColorPalette(it) { color ->
                gradientColor = color
            }
        }
        .build()

    val imagePainter = rememberCoilPainter(request = imageRequest)

    val iconResId =
        if (podcastPlayer.podcastIsPlaying) R.drawable.ic_round_pause else R.drawable.ic_round_play_arrow

    var sliderIsChanging by remember { mutableStateOf(false) }

    var localSliderValue by remember { mutableStateOf(0f) }

    val sliderProgress = if (sliderIsChanging) localSliderValue else podcastPlayer.currentEpisodeProgress

    Box(
        modifier = Modifier
            .fillMaxSize()
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.34f) },
                orientation = Orientation.Vertical
            )
    ) {
        if (swipeableState.currentValue >= 1) {
            LaunchedEffect("key") {
                podcastPlayer.showPlayerFullScreen = false
            }
        }

        PodcastPlayerSatelessContent(
            episode = episode,
            darkTheme = isSystemInDarkTheme(),
            imagePainter = imagePainter,
            gradientColor = gradientColor,
            yOffset = swipeableState.offset.value.roundToInt(),
            playPauseIcon = iconResId,
            playbackProgress = sliderProgress,
            currentTime = podcastPlayer.currentPlaybackFormattedPosition,
            totalTime = podcastPlayer.currentEpisodeFormattedDuration,
            onRewind = {
                podcastPlayer.rewind()
            },
            onForward = {
                podcastPlayer.fastForward()
            },
            onTooglePlayback = {
                podcastPlayer.tooglePlaybackState()
            },
            onSliderChange = { newPosition ->
                localSliderValue = newPosition
                sliderIsChanging = true
            },
            onSliderChangeFinished = {
                podcastPlayer.seekToFraction(localSliderValue)
                sliderIsChanging = false
            }
        ) {
            podcastPlayer.showPlayerFullScreen = false
        }
    }

    LaunchedEffect("playbackPosition") {
        podcastPlayer.updateCurrentPlaybackPosition()
    }

    DisposableEffect(backDispatcher) {
        backDispatcher.addCallback(backCallback)

        onDispose {
            backCallback.remove()
            podcastPlayer.showPlayerFullScreen = false
        }
    }
}

@Composable
fun PodcastPlayerSatelessContent(
    episode: Episode,
    imagePainter: Painter,
    gradientColor: Color,
    yOffset: Int,
    @DrawableRes playPauseIcon: Int,
    playbackProgress: Float,
    currentTime: String,
    totalTime: String,
    darkTheme: Boolean,
    onRewind: () -> Unit,
    onForward: () -> Unit,
    onTooglePlayback: () -> Unit,
    onSliderChange: (Float) -> Unit,
    onSliderChangeFinished: () -> Unit,
    onClose: () -> Unit
) {
    val gradientColors = if (darkTheme) {
        listOf(gradientColor, MaterialTheme.colors.background)
    } else {
        listOf(MaterialTheme.colors.background, MaterialTheme.colors.background)
    }

    val sliderColors = if (darkTheme) {
        SliderDefaults.colors(
            thumbColor = MaterialTheme.colors.onBackground,
            activeTrackColor = MaterialTheme.colors.onBackground,
            inactiveTrackColor = MaterialTheme.colors.onBackground.copy(
                alpha = IndicatorBackgroundOpacity
            ),
        )
    } else SliderDefaults.colors(
        thumbColor = gradientColor,
        activeTrackColor = gradientColor,
        inactiveTrackColor = gradientColor.copy(
            alpha = IndicatorBackgroundOpacity
        ),
    )


    Box(
        modifier = Modifier
            .offset { IntOffset(0, yOffset) }
            .fillMaxSize()
    ) {
        Surface {
            Box(
                modifier = Modifier
                    .background(
                        Brush.verticalGradient(
                            colors = gradientColors,
                            endY = LocalConfiguration.current.screenHeightDp.toFloat() * LocalDensity.current.density / 2
                        )
                    )
                    .fillMaxSize()
                    .systemBarsPadding()
            ) {
                Column {
                    IconButton(
                        imageVector = Icons.Rounded.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.close),
                        onClick = onClose
                    )

                    Column(
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {

                        Box(
                            modifier = Modifier
                                .padding(vertical = 32.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .weight(1f, fill = false)
                                .aspectRatio(1f)
                                .background(MaterialTheme.colors.onBackground.copy(alpha = 0.08f))
                        ) {
                            Image(
                                painter = imagePainter,
                                contentDescription = stringResource(R.string.podcast_thumbnail),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize(),
                            )
                        }

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

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            Slider(
                                value = playbackProgress,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                colors = sliderColors,
                                onValueChange = onSliderChange,
                                onValueChangeFinished = onSliderChangeFinished,
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                EmphasisText(text = currentTime)
                                EmphasisText(text = totalTime)
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_round_replay_10),
                                contentDescription = stringResource(R.string.replay_10_seconds),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = onRewind)
                                    .padding(12.dp)
                                    .size(32.dp)
                            )
                            Icon(
                                painter = painterResource(playPauseIcon),
                                contentDescription = stringResource(R.string.play),
                                tint = MaterialTheme.colors.background,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colors.onBackground)
                                    .clickable(onClick = onTooglePlayback)
                                    .size(64.dp)
                                    .padding(8.dp)
                            )
                            Icon(
                                painter = painterResource(R.drawable.ic_round_forward_10),
                                contentDescription = stringResource(R.string.forward_10_seconds),
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable(onClick = onForward)
                                    .padding(12.dp)
                                    .size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Player")
@Composable
fun PodcastPlayerPreview() {
    PreviewContent(darkTheme = true) {
        PodcastPlayerSatelessContent(
            episode = Episode(
                "1",
                "",
                "",
                "",
                Podcast("", "", "", "This is podcast title", "", "This is publisher"),
                "",
                0,
                "This is a title",
                "",
                2700,
                false,
                "This is a description"
            ),
            imagePainter = painterResource(id = R.drawable.ic_microphone),
            gradientColor = Color.DarkGray,
            yOffset = 0,
            playPauseIcon = R.drawable.ic_round_play_arrow,
            playbackProgress = 0f,
            currentTime = "0:00",
            totalTime = "10:00",
            darkTheme = true,
            onClose = { },
            onForward = { },
            onRewind = { },
            onTooglePlayback = { },
            onSliderChange = { },
            onSliderChangeFinished = { }
        )
    }
}