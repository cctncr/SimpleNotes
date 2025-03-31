package com.example.simplenotes.di

import android.content.Context
import androidx.room.Room.databaseBuilder
import com.example.simplenotes.data.local.dao.NoteDao
import com.example.simplenotes.data.local.database.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): NoteDatabase {
        return databaseBuilder(appContext, NoteDatabase::class.java, "note-database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideNoteDao(database: NoteDatabase): NoteDao {
        return database.noteDao()
    }
}