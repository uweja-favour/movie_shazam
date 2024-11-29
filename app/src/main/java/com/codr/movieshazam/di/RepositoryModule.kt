package com.codr.movieshazam.di

import android.content.Context
import com.codr.movieshazam.RsRepository
import com.codr.movieshazam.RsRepositoryImpl
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
    ) : RsRepository {
        return RsRepositoryImpl(context)
    }
}