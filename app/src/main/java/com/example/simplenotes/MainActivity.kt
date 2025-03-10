package com.example.simplenotes

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var fab: FloatingActionButton
    private val notes = mutableListOf<Note>()

    private val createNoteFromAddNoteActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val title = intent?.getStringExtra("title")
                val text = intent?.getStringExtra("text")
                if (!title.isNullOrBlank() && !text.isNullOrBlank()) {
                    val note = Note(title, text)
                    notes.add(note)
                    notesRecyclerView.adapter?.notifyItemInserted(notes.lastIndex)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initListeners()
    }

    private fun initViews() {
        // Notes RecyclerView
        val notesAdapter = NotesAdapter(notes)
        notesRecyclerView = findViewById(R.id.notesRecyclerView)
        notesRecyclerView.layoutManager = LinearLayoutManager(this)
        notesRecyclerView.adapter = notesAdapter

        // Toolbar
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Fab
        fab = findViewById(R.id.fab)
    }

    private fun initListeners() {
        fab.setOnClickListener {
            createNoteFromAddNoteActivity
                .launch(Intent(this, AddNoteActivity::class.java))
        }
    }
}