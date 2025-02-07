package com.deezer.exoapplication.player

import androidx.media3.common.Player
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject

class PlaybackEndObserver @Inject constructor(): Player.Listener {

    private val _playbackEndedSharedFlow = MutableSharedFlow<Unit>()
    val playbackEndedSharedFlow: SharedFlow<Unit> get() = _playbackEndedSharedFlow

    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        if(state == Player.STATE_ENDED) {
            _playbackEndedSharedFlow.tryEmit(Unit)
        }
    }
}
