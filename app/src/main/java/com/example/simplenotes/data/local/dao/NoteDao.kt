package com.example.simplenotes.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.simplenotes.data.local.entity.Note

@Dao
interface NoteDao {
    @Query("SELECT * FROM note ORDER BY position ASC")
    suspend fun getAll(): List<Note>

    @Insert
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Update
    suspend fun update(note: Note)

    @Query("UPDATE note SET position = :position WHERE id = :id")
    suspend fun updatePosition(id: Int, position: Int)

    @Transaction
    suspend fun updatePositions(noteIds: List<Int>, positions: List<Int>) {
        for (i in noteIds.indices) {
            updatePosition(noteIds[i], positions[i])
        }
    }

    @Query("SELECT * FROM note WHERE id = :id")
    suspend fun getNoteById(id: Int): Note?
}