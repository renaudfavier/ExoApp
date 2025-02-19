package com.deezer.exoapplication.player.di

import com.deezer.exoapplication.player.data.InMemoryTrackRepository
import com.deezer.exoapplication.player.domain.TrackRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PlayerModule {

    @Singleton
    @Binds
    fun bindQuestRepository(memory: InMemoryTrackRepository): TrackRepository

}
