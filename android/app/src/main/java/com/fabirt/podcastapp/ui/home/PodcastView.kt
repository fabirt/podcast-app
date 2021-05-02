package com.fabirt.podcastapp.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.fabirt.podcastapp.domain.model.Episode
import com.fabirt.podcastapp.ui.podcast.PodcastImage

@Composable
fun PodcastView(
    podcast: Episode,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colors.background)
            .clickable(onClick = onClick),
    ) {
        PodcastImage(
            url = podcast.thumbnail,
            aspectRatio = 1f
        )
        Text(
            podcast.titleOriginal,
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(8.dp)
        )
    }
}
