package com.hermes.android.di

import android.content.Context
import androidx.room.Room
import com.hermes.android.data.db.AppDatabase
import com.hermes.android.data.repository.ChatRepositoryImpl
import com.hermes.android.data.repository.SessionRepositoryImpl
import com.hermes.android.domain.repository.ChatRepository
import com.hermes.android.domain.repository.SessionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "hermes.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideChatRepository(impl: ChatRepositoryImpl): ChatRepository = impl

    @Provides
    @Singleton
    fun provideSessionRepository(impl: SessionRepositoryImpl): SessionRepository = impl
}
