package com.example.simplenotes.data.repository

import com.example.simplenotes.data.local.dao.NoteDao
import com.example.simplenotes.data.local.entity.Note

class NoteRepository(private val noteDao: NoteDao) {
    companion object {
        @Volatile
        private var instance: NoteRepository? = null

        fun getInstance(noteDao: NoteDao): NoteRepository {
            return instance ?: synchronized(this) {
                instance ?: NoteRepository(noteDao).also { instance = it }
            }
        }
    }

    suspend fun getAllNotes(): List<Note> {
        return noteDao.getAll()
    }

    suspend fun insertNote(note: Note) {
        noteDao.insert(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    suspend fun updateNotePositions(notes: List<Note>) {
        val ids = notes.map { it.id }
        val positions = List(notes.size) { index -> index }
        noteDao.updatePositions(ids, positions)
    }
}