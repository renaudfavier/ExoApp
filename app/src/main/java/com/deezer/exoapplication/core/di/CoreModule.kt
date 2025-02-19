package com.deezer.exoapplication.core.di

import com.deezer.exoapplication.core.data.MetadataReaderImpl
import com.deezer.exoapplication.core.domain.MetaDataReader
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface CoreModule {

    @Singleton
    @Binds
    fun bindMetadataReader(metadataReader: MetadataReaderImpl): MetaDataReader

}