package com.fabirt.podcastapp.ui.podcast

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.runtime.*
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

        PodcastPlayerSatelessContent(
            episode = episode,
            imagePainter = imagePainter,
            gradientColor = gradientColor,
            yOffset = swipeableState.offset.value.roundToInt()
        ) {
            podcastPlayer.showPlayerFullScreen = false
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

@Composable
fun PodcastPlayerSatelessContent(
    episode: Episode,
    imagePainter: Painter,
    gradientColor: Color,
    yOffset: Int,
    onClose: () -> Unit
) {
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
                            listOf(gradientColor, MaterialTheme.colors.background),
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
            yOffset = 0
        ) {

        }
    }
}