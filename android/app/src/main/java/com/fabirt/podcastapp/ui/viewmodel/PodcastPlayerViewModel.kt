package com.fabirt.podcastapp.ui.viewmodel

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v4.media.MediaBrowserCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import com.fabirt.podcastapp.constant.K
import com.fabirt.podcastapp.data.service.MediaPlayerServiceConnection
import com.fabirt.podcastapp.domain.model.Episode
import com.fabirt.podcastapp.util.isPlayEnabled
import com.fabirt.podcastapp.util.isPlaying
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PodcastPlayerViewModel @Inject constructor(
    private val serviceConnection: MediaPlayerServiceConnection
) : ViewModel() {

    val currentPlayingEpisode = serviceConnection.currentPlayingEpisode
    val playbackState = serviceConnection.playbackState

    var showPlayerFullScreen by mutableStateOf(false)

    val podcastisPlaying: Boolean
        get() = playbackState.value?.isPlaying == true

    fun playPodcast(episodes: List<Episode>, currentEpisode: Episode) {
        serviceConnection.playPodcast(episodes)
        if (currentEpisode.id == currentPlayingEpisode.value?.id) {
            if (podcastisPlaying) {
                serviceConnection.transportControls.pause()
            } else {
                serviceConnection.transportControls.play()
            }
        } else {
            serviceConnection.transportControls.playFromMediaId(currentEpisode.id, null)
        }
    }

    fun tooglePlaybackState() {
        when {
            playbackState.value?.isPlaying == true -> {
                serviceConnection.transportControls.pause()
            }

            playbackState.value?.isPlayEnabled == true -> {
                serviceConnection.transportControls.play()
            }
        }
    }

    fun stopPlayback() {
        serviceConnection.transportControls.stop()
    }

    fun calculateColorPalette(drawable: Drawable, onFinised: (Color) -> Unit) {
        val bitmap = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)
        Palette.from(bitmap).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinised(Color(colorValue))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        serviceConnection.unsubscribe(
            K.MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})
    }
}