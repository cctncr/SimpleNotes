package com.example.simplenotes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenotes.data.local.entity.Note
import com.example.simplenotes.data.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {
    private val _allNotes = MutableLiveData<List<Note>>()
    val allNotes: LiveData<List<Note>> = _allNotes

    private fun loadNotes() {
        viewModelScope.launch {
            _allNotes.value = repository.getAllNotes()
        }
    }

    fun insertNote(note: Note) {
        viewModelScope.launch {
            repository.insertNote(note)
            loadNotes()
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)
            loadNotes()
        }
    }

    fun reorderNotes(notes: List<Note>) {
        viewModelScope.launch {
            notes.forEachIndexed { index, note ->
                note.position = index
            }

            repository.updateNotePositions(notes)
            loadNotes()
        }
    }

    init {
        loadNotes()
    }
}