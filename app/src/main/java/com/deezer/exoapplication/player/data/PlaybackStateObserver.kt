package com.deezer.exoapplication.player.data

import androidx.media3.common.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class PlaybackStateObserver @Inject constructor(
    player: Player,
): Player.Listener {

    init {
        player.addListener(this)
    }

    private val _playerPlaybackStateFlow = MutableStateFlow(Player.STATE_IDLE)
    val playerPlaybackStateFlow: StateFlow<Int> = _playerPlaybackStateFlow

    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        _playerPlaybackStateFlow.tryEmit(state)
    }
}
