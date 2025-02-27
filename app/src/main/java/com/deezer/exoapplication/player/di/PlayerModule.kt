package com.deezer.exoapplication.player.di

import com.deezer.exoapplication.player.data.InMemoryTrackRepository
import com.deezer.exoapplication.player.domain.IsPlayingRepository
import com.deezer.exoapplication.player.data.IsPlayingRepositoryImpl
import com.deezer.exoapplication.player.data.SongEndedRepositoryImpl
import com.deezer.exoapplication.player.domain.SongEndedRepository
import com.deezer.exoapplication.player.domain.QueueManager
import com.deezer.exoapplication.player.domain.TrackRepository
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
    fun bindSongRepository(impl: SongEndedRepositoryImpl): SongEndedRepository

    @Singleton
    @Binds
    fun bindSongRepository(impl: IsPlayingRepositoryImpl): IsPlayingRepository

    companion object {
        @Provides
        @Singleton
        fun provideQueueManager() = QueueManager()
    }

}
