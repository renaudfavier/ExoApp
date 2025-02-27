package com.deezer.exoapplication.player.domain.repository

import kotlinx.coroutines.flow.Flow

interface IsPlayingRepository {
    fun observeIsPlaying(): Flow<Boolean>
}