package com.example.simplenotes.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.simplenotes.data.local.dao.NoteDao
import com.example.simplenotes.data.local.entity.Note

@Database(entities = [Note::class], version = 3)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}