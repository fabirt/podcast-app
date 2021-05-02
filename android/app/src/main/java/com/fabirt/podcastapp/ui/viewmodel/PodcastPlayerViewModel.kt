package com.fabirt.podcastapp.ui.viewmodel

import android.support.v4.media.MediaBrowserCompat
import androidx.lifecycle.ViewModel
import com.fabirt.podcastapp.constant.K
import com.fabirt.podcastapp.data.service.MediaPlayerServiceConnection
import com.fabirt.podcastapp.domain.model.Episode
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PodcastPlayerViewModel @Inject constructor(
    private val serviceConnection: MediaPlayerServiceConnection
) : ViewModel() {

    val currentPlayingEpisode = serviceConnection.currentPlayingEpisode
    val playbackState = serviceConnection.playbackState

    fun playPodcast(episodes: List<Episode>, currentEpisode: Episode) {
        serviceConnection.playPodcast(episodes)
        serviceConnection.transportControls.playFromMediaId(currentEpisode.id, null)
    }

    fun stopPlayback() {
        serviceConnection.transportControls.stop()
    }

    override fun onCleared() {
        super.onCleared()
        serviceConnection.unsubscribe(
            K.MEDIA_ROOT_ID,
            object : MediaBrowserCompat.SubscriptionCallback() {})
    }
}