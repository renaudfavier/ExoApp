package com.deezer.exoapplication.player.data

import com.deezer.exoapplication.player.domain.SongEndedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongEndedRepositoryImpl @Inject constructor(
    private val songEndedObserver: SongEndedDataSource
): SongEndedRepository {

    override fun observeSongEnded(): Flow<Unit> {
        return songEndedObserver.getSongEndedFlow()
    }
}
