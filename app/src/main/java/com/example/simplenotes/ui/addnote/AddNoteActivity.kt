package com.example.simplenotes.ui.addnote

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.simplenotes.databinding.ActivityAddNoteBinding

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()
    }

    private fun initListeners() {
        binding.fab.setOnClickListener {
            Intent().apply {
                putExtra("title", binding.etTitle.text.toString())
                putExtra("text", binding.etText.text.toString())
                setResult(RESULT_OK, this)
            }
            finish()
        }
    }
}