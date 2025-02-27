package com.deezer.exoapplication.player.data.repository

import com.deezer.exoapplication.player.data.datasource.SongEndedDataSource
import com.deezer.exoapplication.player.domain.repository.SongEndedRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SongEndedRepositoryImpl @Inject constructor(
    private val songEndedObserver: SongEndedDataSource
): SongEndedRepository {

    override fun observeSongEnded(): Flow<Unit> {
        return songEndedObserver.getSongEndedFlow()
    }
}
