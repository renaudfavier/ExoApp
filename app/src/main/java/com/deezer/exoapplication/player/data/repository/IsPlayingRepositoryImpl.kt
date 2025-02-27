package com.deezer.exoapplication.player.data.repository

import com.deezer.exoapplication.player.data.datasource.IsPlayingDataSource
import com.deezer.exoapplication.player.domain.repository.IsPlayingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsPlayingRepositoryImpl @Inject constructor(
    private val isPlayingDataSource: IsPlayingDataSource
): IsPlayingRepository {
    override fun observeIsPlaying(): Flow<Boolean> {
        return isPlayingDataSource.getIsPlayingFlow()
    }
}
