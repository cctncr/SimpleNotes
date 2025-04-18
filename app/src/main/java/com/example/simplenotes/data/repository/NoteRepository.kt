package com.example.simplenotes.data.repository

import com.example.simplenotes.data.local.dao.NoteDao
import com.example.simplenotes.data.local.entity.Note
import javax.inject.Inject

class NoteRepository @Inject constructor(
    private val noteDao: NoteDao
) {
    suspend fun getAllNotes(): List<Note> {
        return noteDao.getAll()
    }

    suspend fun insertNote(note: Note): Note {
        val id = noteDao.insert(note)
        return note.copy(id = id.toInt())
    }

    suspend fun deleteNote(note: Note) {
        noteDao.delete(note)
    }

    suspend fun updateNotePositions(notes: List<Note>) {
        val ids = notes.map { it.id }
        val positions = List(notes.size) { index -> index }
        noteDao.updatePositions(ids, positions)
    }

    suspend fun getNoteById(id: Int): Note? {
        return noteDao.getNoteById(id)
    }

    suspend fun updateNote(note: Note) {
        noteDao.update(note)
    }

    suspend fun searchNotes(query: String): List<Note> {
        return noteDao.searchNotes(query)
    }
}