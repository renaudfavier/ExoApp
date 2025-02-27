package com.deezer.exoapplication.player.data

import androidx.media3.common.Player
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SongEndedDataSource @Inject constructor(
    private val player: Player
) {

    fun getSongEndedFlow(): Flow<Unit> = callbackFlow {
        val listener = object: Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                if(state == Player.STATE_ENDED) {
                    trySend(Unit)
                }
            }
        }
        player.addListener(listener)
        awaitClose {
            player.removeListener(listener)
        }
    }
}
