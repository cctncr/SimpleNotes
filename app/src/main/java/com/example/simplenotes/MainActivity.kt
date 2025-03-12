package com.example.simplenotes

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var fab: FloatingActionButton
    private lateinit var db: NoteDatabase
    private lateinit var noteDao: NoteDao
    private lateinit var noteList: MutableList<Note>

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

        initDatabase()
        initViews()
        initListeners()
    }

    private fun initViews() {
        // Notes RecyclerView
        notesRecyclerView = findViewById<RecyclerView?>(R.id.notesRecyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = NotesAdapter(noteList)
        }

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

    private fun initDatabase() {
        db = Room
            .databaseBuilder(
                applicationContext,
                NoteDatabase::class.java,
                "note-database"
            ).build()

        noteDao = db.noteDao()

        runBlocking {
            noteList = noteDao.getAll().toMutableList()
        }
    }

    private fun handleNoteCreationResult(intent: Intent) {
        val title = intent.getStringExtra("title")
        val text = intent.getStringExtra("text")
        if (!title.isNullOrBlank() && !text.isNullOrBlank()) {
            val note = Note(title, text)
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    noteDao.insert(note)
                    noteList.add(note)
                }
                notesRecyclerView.adapter?.notifyItemInserted(noteList.lastIndex)
            }
        }
    }
}