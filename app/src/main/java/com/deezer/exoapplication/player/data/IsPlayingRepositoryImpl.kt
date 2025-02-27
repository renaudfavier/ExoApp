package com.deezer.exoapplication.player.data

import com.deezer.exoapplication.player.domain.IsPlayingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsPlayingRepositoryImpl @Inject constructor(
    private val isPlayingDataSource: IsPlayingDataSource
): IsPlayingRepository {
    override fun observeIsPlaying(): Flow<Boolean> {
        return isPlayingDataSource.getIsPlayingFlow()
    }
}
