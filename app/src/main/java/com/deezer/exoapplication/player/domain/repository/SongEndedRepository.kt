package com.deezer.exoapplication.player.domain.repository

import kotlinx.coroutines.flow.Flow

interface SongEndedRepository {
    fun observeSongEnded(): Flow<Unit>
}