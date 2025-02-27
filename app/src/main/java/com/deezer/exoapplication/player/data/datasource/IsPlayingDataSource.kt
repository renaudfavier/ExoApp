package com.deezer.exoapplication.player.data.datasource

import androidx.media3.common.Player
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class IsPlayingDataSource @Inject constructor(
    private val player: Player
) {
    fun getIsPlayingFlow(): Flow<Boolean> = callbackFlow {
        trySend(player.isPlaying)
        val listener = object: Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
                trySend(isPlaying)
            }
        }
        player.addListener(listener)
        awaitClose {
            player.removeListener(listener)
        }
    }
}
