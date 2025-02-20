package com.deezer.exoapplication.player.data

import androidx.media3.common.Player
import com.deezer.exoapplication.player.domain.SongEndedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongEndedRepositoryImpl @Inject constructor(
    private val player: Player,
    private val songEndedObserver: SongEndedObserver
): SongEndedRepository {

    override fun observeSongEnded(): Flow<Unit> {
        return songEndedObserver.observeAsFlow(player)
    }
}
