package com.deezer.exoapplication.player.data

import androidx.media3.common.Player
import com.deezer.exoapplication.player.domain.model.Track
import javax.inject.Inject

class MediaPlayer @Inject constructor(
    private val player: Player,
    private val mediaItemFactory: MediaItemFactory,
) {
    init {
        player.prepare()
    }

    fun play(track: Track) {
        val mediaItem = mediaItemFactory.createFromUri(track.uri)
        player.setMediaItem(mediaItem)
        player.play()
    }

    fun stop() {
        player.clearMediaItems()
    }

    fun pause() {
        player.pause()
    }

    fun play() {
        player.play()
    }

    fun release() {
        player.release()
    }

}