package com.example.simplenotes.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.simplenotes.ui.addnote.AddNoteActivity
import com.example.simplenotes.R
import com.example.simplenotes.data.local.database.NoteDatabase
import com.example.simplenotes.data.local.entity.Note
import com.example.simplenotes.data.repository.NoteRepository
import com.example.simplenotes.viewmodel.NoteViewModel
import com.example.simplenotes.viewmodel.NoteViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var fab: FloatingActionButton
    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NotesAdapter

    private val createNoteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                handleNoteCreationResult(intent!!)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViewModel()
        initViews()
        initListeners()
        observeViewModel()
    }

    private fun setupViewModel() {
        val db = Room
            .databaseBuilder(applicationContext, NoteDatabase::class.java, "note-database")
            .fallbackToDestructiveMigration()
            .build()

        val noteDao = db.noteDao()

        val repository = NoteRepository.getInstance(noteDao)

        val factory = NoteViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]
    }

    private fun observeViewModel() {
        viewModel.allNotes.observe(this) { notes ->
            adapter.updateNotes(notes)
        }
    }

    private fun initViews() {
        // Notes RecyclerView
        adapter = NotesAdapter(
            emptyList<Note>().toMutableList(),
            onNoteDeleted = { note -> viewModel.deleteNote(note) },
            onNotesReordered = { notes -> viewModel.reorderNotes(notes) }
        )
        notesRecyclerView = findViewById(R.id.notesRecyclerView)
        notesRecyclerView.layoutManager = LinearLayoutManager(this)
        notesRecyclerView.adapter = adapter
        val callback: ItemTouchHelper.Callback = NoteTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(notesRecyclerView)

        // Toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Fab
        fab = findViewById(R.id.fab)
    }

    private fun initListeners() {
        fab.setOnClickListener {
            createNoteLauncher
                .launch(Intent(this, AddNoteActivity::class.java))
        }
    }

    private fun handleNoteCreationResult(intent: Intent) {
        val title = intent.getStringExtra("title")
        val text = intent.getStringExtra("text")
        if (!title.isNullOrBlank() && !text.isNullOrBlank()) {
            val note = Note(title, text)
            viewModel.insertNote(note)
        }
    }
}