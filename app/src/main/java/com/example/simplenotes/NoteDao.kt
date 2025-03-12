package com.example.simplenotes

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NoteDao {
    @Query("SELECT * FROM note")
    suspend fun getAll(): List<Note>

    @Insert
    suspend fun insert(note: Note)
}