package com.fabirt.podcastapp.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun PrimaryButton(
    text: String,
    height: Dp = 58.dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .defaultMinSize(200.dp)
            .height(height)
            .clip(CircleShape)
            .background(MaterialTheme.colors.primary)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = MaterialTheme.colors.onPrimary,
            style = MaterialTheme.typography.button,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .align(Alignment.Center)
        )
    }
}