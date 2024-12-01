package com.codr.movieshazam.di

import android.content.Context
import com.codr.movieshazam.RsDataSource
import com.codr.movieshazam.RsDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideRsRepository(
        @ApplicationContext context: Context
    ) : RsDataSource {
        return RsDataSourceImpl(context)
    }
}