package com.fabirt.podcastapp.data.exoplayer

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.fabirt.podcastapp.domain.model.Episode
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PodcastMediaSource @Inject constructor() {
    var mediaMetadataEpisodes: List<MediaMetadataCompat> = emptyList()
    var podcastEpisodes: List<Episode> = emptyList()
        private set
    private val onReadyListeners = mutableListOf<OnReadyListener>()

    private var state: MusicSourceState =
        MusicSourceState.CREATED
        set(value) {
            if (value == MusicSourceState.INITIALIZED || value == MusicSourceState.ERROR) {
                synchronized(onReadyListeners) {
                    field = value
                    onReadyListeners.forEach { listener ->
                        listener(isReady)
                    }
                }
            } else {
                field = value
            }
        }

    private val isReady: Boolean
        get() = state == MusicSourceState.INITIALIZED

    fun setEpisodes(data: List<Episode>) {
        state = MusicSourceState.INITIALIZING
        podcastEpisodes = data
        mediaMetadataEpisodes = data.map { episode ->
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, episode.id)
                .putString(
                    MediaMetadataCompat.METADATA_KEY_ARTIST,
                    episode.podcast.publisherOriginal
                )
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, episode.titleOriginal)
                .putString(
                    MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE,
                    episode.podcast.titleOriginal
                )
                .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, episode.audio)
                .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, episode.image)
                .build()
        }
        state = MusicSourceState.INITIALIZED
    }

    fun asMediaSource(dataSourceFactory: DataSource.Factory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        mediaMetadataEpisodes.forEach { metadata ->
            val mediaItem = MediaItem.fromUri(
                metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri()
            )
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }

    fun asMediaItems() = mediaMetadataEpisodes.map { metadata ->
        val description = MediaDescriptionCompat.Builder()
            .setMediaId(metadata.description.mediaId)
            .setTitle(metadata.description.title)
            .setSubtitle(metadata.description.subtitle)
            .setIconUri(metadata.description.iconUri)
            .setMediaUri(metadata.description.mediaUri)
            .build()
        MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }.toMutableList()

    fun whenReady(listener: OnReadyListener): Boolean {
        return if (state == MusicSourceState.CREATED || state == MusicSourceState.INITIALIZING) {
            onReadyListeners += listener
            false
        } else {
            listener(isReady)
            true
        }
    }

    fun refresh() {
        onReadyListeners.clear()
        state = MusicSourceState.CREATED
    }
}

typealias OnReadyListener = (Boolean) -> Unit

enum class MusicSourceState {
    CREATED,
    INITIALIZING,
    INITIALIZED,
    ERROR
}