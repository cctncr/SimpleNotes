package com.example.simplenotes.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplenotes.ui.addnote.AddNoteActivity
import com.example.simplenotes.data.local.entity.Note
import com.example.simplenotes.databinding.ActivityMainBinding
import com.example.simplenotes.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<NoteViewModel>()
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

        initViews()
        initListeners()
        observeViewModel()
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