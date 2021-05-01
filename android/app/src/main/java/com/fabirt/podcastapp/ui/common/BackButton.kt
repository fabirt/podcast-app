package com.fabirt.podcastapp.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.fabirt.podcastapp.R
import com.fabirt.podcastapp.ui.navigation.Navigator

@Composable
fun BackButton() {
    val navController = Navigator.current
    Icon(
        Icons.Default.ArrowBack,
        contentDescription = stringResource(R.string.back),
        modifier = Modifier
            .padding(top = 8.dp)
            .clip(CircleShape)
            .clickable {
                navController.navigateUp()
            }
            .padding(16.dp)
    )
}