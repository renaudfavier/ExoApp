package com.deezer.exoapplication.player.di

import com.deezer.exoapplication.player.data.repository.InMemoryTrackRepository
import com.deezer.exoapplication.player.domain.repository.IsPlayingRepository
import com.deezer.exoapplication.player.data.repository.IsPlayingRepositoryImpl
import com.deezer.exoapplication.player.data.repository.SongEndedRepositoryImpl
import com.deezer.exoapplication.player.domain.repository.SongEndedRepository
import com.deezer.exoapplication.player.domain.QueueManager
import com.deezer.exoapplication.player.domain.repository.TrackRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PlayerModule {

    @Singleton
    @Binds
    fun bindQuestRepository(memory: InMemoryTrackRepository): TrackRepository

    @Singleton
    @Binds
    fun bindSongEndedRepository(impl: SongEndedRepositoryImpl): SongEndedRepository

    @Singleton
    @Binds
    fun bindIsPlayingRepository(impl: IsPlayingRepositoryImpl): IsPlayingRepository

    companion object {
        @Provides
        @Singleton
        fun provideQueueManager() = QueueManager()
    }

}
