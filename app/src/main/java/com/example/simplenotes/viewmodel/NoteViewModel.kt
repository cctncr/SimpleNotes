package com.example.simplenotes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenotes.data.local.entity.Note
import com.example.simplenotes.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {
    private val _allNotes = MutableLiveData<List<Note>>()
    val allNotes: LiveData<List<Note>> = _allNotes

    private val _currentNote = MutableLiveData<Note?>()
    val currentNote: LiveData<Note?> = _currentNote

    private val _searchResults = MutableLiveData<List<Note>>()
    val searchResults: LiveData<List<Note>> = _searchResults

    private var _isSearchActive = MutableLiveData<Boolean>(false)
    val isSearchActive: LiveData<Boolean> = _isSearchActive

    init {
        loadNotes()
    }

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

    fun validateNoteInput(title: String, text: String): Boolean {
        return title.isNotEmpty() || text.isNotEmpty()
    }

    fun createAndSaveNote(title: String, text: String): Boolean {
        return if (validateNoteInput(title, text)) {
            val note = Note(title = title, text = text)
            insertNote(note)
            true
        } else {
            false
        }
    }

    fun getNoteById(id: Int): LiveData<Note?> {
        viewModelScope.launch {
            _currentNote.value = repository.getNoteById(id)
        }
        return currentNote
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)
            loadNotes()
        }
    }

    fun hasUnsavedChanges(originalNote: Note?, currentTitle: String, currentText: String): Boolean {
        val originalTitle = originalNote?.title ?: ""
        val originalText = originalNote?.text ?: ""

        return originalTitle != currentTitle || originalText != currentText
    }

    fun clearSearch() {
        _isSearchActive.value = false
        _searchResults.value = emptyList()
        loadNotes()
    }

    fun searchNotes(query: String) {
        if (query.isBlank()) {
            clearSearch()
            return
        }

        viewModelScope.launch {
            _isSearchActive.value = true
            _searchResults.value = repository.searchNotes(query)
        }
    }
}