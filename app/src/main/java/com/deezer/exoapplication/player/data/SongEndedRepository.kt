package com.deezer.exoapplication.player.data

import androidx.media3.common.Player
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongEndedRepository @Inject constructor(
    private val player: Player,
    private val songEndedObserver: SongEndedObserver
) {

    fun observeSongEnded(): Flow<Unit> {
        return songEndedObserver.observeAsFlow(player)
    }
}