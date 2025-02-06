package com.deezer.exoapplication

import android.app.Application
import com.deezer.exoapplication.player.MetaDataReader
import com.deezer.exoapplication.player.MetadataReaderImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AppModule {

    @Provides
    @ViewModelScoped
    fun provideMetaDataReader(app: Application): MetaDataReader {
        return MetadataReaderImpl(app)
    }
}
