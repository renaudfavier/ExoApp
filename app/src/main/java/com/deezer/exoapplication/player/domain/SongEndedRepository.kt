package com.deezer.exoapplication.player.domain

import kotlinx.coroutines.flow.Flow

interface SongEndedRepository {
    fun observeSongEnded(): Flow<Unit>
}