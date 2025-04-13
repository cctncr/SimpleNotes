package com.example.simplenotes.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.example.simplenotes.data.local.entity.Note
import com.example.simplenotes.data.repository.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {
    private val _allNotes = MutableLiveData<List<Note>>(emptyList())

    private val _searchQuery = MutableLiveData("")
    private val _isSearchActive = MutableLiveData(false)
    private var searchJob: Job? = null

    private val _viewState = MediatorLiveData(NotesViewState()).apply {
        value = NotesViewState()
        addSource(_allNotes) { notes ->
            if (_isSearchActive.value != true) {
                value = value?.copy(
                    notes = notes,
                    isEmptyStateVisible = notes.isEmpty(),
                    emptyStateMessage = if (notes.isEmpty()) "No notes yet" else ""
                )
            }
        }
    }
    val viewState: LiveData<NotesViewState> = _viewState

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            _allNotes.value = repository.getAllNotes()
        }
    }

    private fun insertNote(note: Note) {
        viewModelScope.launch {
            val insertedNote = repository.insertNote(note)
            _viewState.value = _viewState.value?.let { currentState ->
                if (!currentState.isSearchActive) {
                    val updatedNotes = currentState.notes + insertedNote
                    currentState.copy(
                        notes = updatedNotes,
                        isEmptyStateVisible = false
                    )
                } else {
                    currentState
                }
            }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.deleteNote(note)

            _viewState.value = _viewState.value?.let { currentState ->
                val updatedNotes = currentState.notes.filter { it.id != note.id }
                currentState.copy(
                    notes = updatedNotes,
                    isEmptyStateVisible = updatedNotes.isEmpty() && !currentState.isSearchActive,
                    emptyStateMessage = if (updatedNotes.isEmpty() && !currentState.isSearchActive)
                        "No notes yet" else currentState.emptyStateMessage
                )
            }
        }
    }

    fun reorderNotes(notes: List<Note>) {
        viewModelScope.launch {
            repository.updateNotePositions(notes)
            _viewState.value = _viewState.value?.copy(notes = notes)
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
            val currentNote = repository.getNoteById(id)
            _viewState.value = _viewState.value?.copy(currentNote = currentNote)
        }
        return _viewState.map { it.currentNote }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            repository.updateNote(note)

            _viewState.value = _viewState.value?.let { currentState ->
                val updatedNotes = currentState.notes.map {
                    if (it.id == note.id) note else it
                }

                currentState.copy(
                    notes = updatedNotes,
                    currentNote = if (currentState.currentNote?.id == note.id) {
                        note
                    } else {
                        currentState.currentNote
                    }
                )
            }
        }
    }

    fun hasUnsavedChanges(originalNote: Note?, currentTitle: String, currentText: String): Boolean {
        val originalTitle = originalNote?.title ?: ""
        val originalText = originalNote?.text ?: ""

        return originalTitle != currentTitle || originalText != currentText
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()
        if (query.isBlank()) {
            clearSearch()
            return
        }
        searchJob = viewModelScope.launch {
            delay(300) // Debounce
            performSearch(query)
        }
    }

    fun onSearchQuerySubmitted(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                clearSearch()
                return@launch
            }

            val results = repository.searchNotes(query)
            _viewState.value = _viewState.value?.copy(
                notes = results,
                isSearchActive = true,
                searchQuery = query,
                searchTitle = "Search Results for \"$query\"",
                isFabVisible = false,
                isEmptyStateVisible = results.isEmpty(),
                emptyStateMessage = if (results.isEmpty()) "No notes found for \"$query\"" else ""
            )
        }
    }

    private suspend fun performSearch(query: String) {
        val results = repository.searchNotes(query)
        _isSearchActive.value = true

        _viewState.value = _viewState.value?.copy(
            notes = results,
            isSearchActive = true,
            searchQuery = query,
            searchTitle = "Search Results for \"$query\"",
            isFabVisible = false,
            isEmptyStateVisible = results.isEmpty(),
            emptyStateMessage = if (results.isEmpty()) "No notes found for \"$query\"" else ""
        )
    }

    fun clearSearch() {
        viewModelScope.launch {
            val allNotes = repository.getAllNotes()

            _viewState.value = _viewState.value?.copy(
                notes = allNotes,
                isSearchActive = false,
                searchQuery = "",
                searchTitle = "Notes",
                isFabVisible = true,
                isEmptyStateVisible = allNotes.isEmpty(),
                emptyStateMessage = if (allNotes.isEmpty()) "No notes yet" else ""
            )
        }
    }
}

data class NotesViewState(
    val notes: List<Note> = emptyList(),
    val currentNote: Note? = null,
    val isSearchActive: Boolean = false,
    val searchQuery: String = "",
    val searchTitle: String = "Notes",
    val isEmptyStateVisible: Boolean = false,
    val emptyStateMessage: String = "",
    val isFabVisible: Boolean = true
)