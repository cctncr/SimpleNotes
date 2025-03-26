package com.example.simplenotes.ui.addnote

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.simplenotes.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddNoteActivity : AppCompatActivity() {
    private lateinit var fab: FloatingActionButton
    private lateinit var etTitle: EditText
    private lateinit var etText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        initViews()
        initListeners()
    }

    private fun initViews() {
        fab = findViewById(R.id.fab)
        etTitle = findViewById(R.id.etTitle)
        etText = findViewById(R.id.etText)
    }

    private fun initListeners() {
        fab.setOnClickListener {
            Intent().apply {
                putExtra("title", etTitle.text.toString())
                putExtra("text", etText.text.toString())
                setResult(RESULT_OK, this)
            }
            finish()
        }
    }
}