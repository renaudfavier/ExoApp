package com.deezer.exoapplication.player.data

import androidx.media3.common.Player
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class SongEndedObserver @Inject constructor() {

    fun observeAsFlow(player: Player): Flow<Unit> = callbackFlow {
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
