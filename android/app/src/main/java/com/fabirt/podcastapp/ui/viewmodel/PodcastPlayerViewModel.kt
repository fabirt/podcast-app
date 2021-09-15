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
import com.fabirt.podcastapp.data.service.MediaPlayerService
import com.fabirt.podcastapp.data.service.MediaPlayerServiceConnection
import com.fabirt.podcastapp.domain.model.Episode
import com.fabirt.podcastapp.util.currentPosition
import com.fabirt.podcastapp.util.isPlayEnabled
import com.fabirt.podcastapp.util.isPlaying
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PodcastPlayerViewModel @Inject constructor(
    private val serviceConnection: MediaPlayerServiceConnection
) : ViewModel() {

    val currentPlayingEpisode = serviceConnection.currentPlayingEpisode

    var showPlayerFullScreen by mutableStateOf(false)

    var currentPlaybackPosition by mutableStateOf(0L)

    val podcastIsPlaying: Boolean
        get() = playbackState.value?.isPlaying == true

    val currentEpisodeProgress: Float
        get() {
            if (currentEpisodeDuration > 0) {
                return currentPlaybackPosition.toFloat() / currentEpisodeDuration
            }
            return 0f
        }

    val currentPlaybackFormattedPosition: String
        get() = formatLong(currentPlaybackPosition)

    val currentEpisodeFormattedDuration: String
        get() = formatLong(currentEpisodeDuration)

    private val playbackState = serviceConnection.playbackState

    private val currentEpisodeDuration: Long
        get() = MediaPlayerService.currentDuration

    fun playPodcast(episodes: List<Episode>, currentEpisode: Episode) {
        serviceConnection.playPodcast(episodes)
        if (currentEpisode.id == currentPlayingEpisode.value?.id) {
            if (podcastIsPlaying) {
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
            palette?.darkVibrantSwatch?.rgb?.let { colorValue ->
                onFinised(Color(colorValue))
            }
        }
    }

    fun fastForward() {
        serviceConnection.fastForward()
    }

    fun rewind() {
        serviceConnection.rewind()
    }

    /**
     * @param value 0.0 to 1.0
     */
    fun seekToFraction(value: Float) {
        serviceConnection.transportControls.seekTo(
            (currentEpisodeDuration * value).toLong()
        )
    }

    suspend fun updateCurrentPlaybackPosition() {
        val currentPosition = playbackState.value?.currentPosition
        if (currentPosition != null && currentPosition != currentPlaybackPosition) {
            currentPlaybackPosition = currentPosition
        }
        delay(K.PLAYBACK_POSITION_UPDATE_INTERVAL)
        updateCurrentPlaybackPosition()
    }

    private fun formatLong(value: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return dateFormat.format(value)
    }

    override fun onCleared() {
        super.onCleared()
        serviceConnection.unsubscribe(
            K.MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})
    }
}