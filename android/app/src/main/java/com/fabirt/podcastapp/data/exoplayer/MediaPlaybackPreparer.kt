package com.fabirt.podcastapp.data.exoplayer

import android.net.Uri
import android.os.Bundle
import android.os.ResultReceiver
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.google.android.exoplayer2.ControlDispatcher
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector

class MediaPlaybackPreparer(
    private val mediaSource: PodcastMediaSource,
    private val playerPrepared: (MediaMetadataCompat?) -> Unit
) : MediaSessionConnector.PlaybackPreparer {
    override fun onPrepareFromSearch(query: String, playWhenReady: Boolean, extras: Bundle?) = Unit

    override fun onCommand(
        player: Player,
        controlDispatcher: ControlDispatcher,
        command: String,
        extras: Bundle?,
        cb: ResultReceiver?
    ): Boolean = false

    override fun getSupportedPrepareActions(): Long {
        return PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID or PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
    }

    override fun onPrepareFromMediaId(mediaId: String, playWhenReady: Boolean, extras: Bundle?) {
        mediaSource.whenReady {
            val itemToPlay = mediaSource.mediaMetadataEpisodes.find {
                it.description.mediaId == mediaId
            }
            playerPrepared(itemToPlay)
        }
    }

    override fun onPrepareFromUri(uri: Uri, playWhenReady: Boolean, extras: Bundle?) = Unit

    override fun onPrepare(playWhenReady: Boolean) = Unit
}