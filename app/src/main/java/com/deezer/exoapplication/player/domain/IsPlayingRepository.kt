package com.deezer.exoapplication.player.domain

import kotlinx.coroutines.flow.Flow

interface IsPlayingRepository {
    fun observeIsPlaying(): Flow<Boolean>
}