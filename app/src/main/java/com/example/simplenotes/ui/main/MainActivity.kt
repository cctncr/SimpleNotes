package com.example.simplenotes.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.example.simplenotes.ui.addnote.AddNoteActivity
import com.example.simplenotes.data.local.database.NoteDatabase
import com.example.simplenotes.data.local.entity.Note
import com.example.simplenotes.data.repository.NoteRepository
import com.example.simplenotes.databinding.ActivityMainBinding
import com.example.simplenotes.viewmodel.NoteViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NotesAdapter
    private lateinit var binding: ActivityMainBinding

    private val createNoteLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                handleNoteCreationResult(intent!!)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = adapter
        val callback: ItemTouchHelper.Callback = NoteTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.notesRecyclerView)

        // Toolbar
        setSupportActionBar(binding.toolbar)
    }

    private fun initListeners() {
        binding.fab.setOnClickListener {
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