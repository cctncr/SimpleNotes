package com.example.simplenotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.simplenotes.R
import com.example.simplenotes.data.local.entity.Note
import com.example.simplenotes.databinding.FragmentEditNoteBinding
import com.example.simplenotes.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditNoteFragment : Fragment() {
    private var _binding: FragmentEditNoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NoteViewModel by viewModels()
    private val args: EditNoteFragmentArgs by navArgs()

    private var currentNote: Note? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadNoteData()
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadNoteData() {
        val noteId = args.noteId
        viewModel.getNoteById(noteId).observe(viewLifecycleOwner) { note ->
            note?.let {
                currentNote = it
                populateFields(it)
            }
        }
    }

    private fun populateFields(note: Note) {
        binding.etTitle.setText(note.title)
        binding.etText.setText(note.text)
    }

    private fun setupListeners() {
        binding.fabSave.setOnClickListener {
            saveChanges()
        }
    }

    private fun saveChanges() {
        val title = binding.etTitle.text.toString()
        val text = binding.etText.text.toString()

        if (viewModel.validateNoteInput(title, text)) {
            currentNote?.let { note ->
                val updatedNote = note.copy(
                    title = title,
                    text = text
                )

                viewModel.updateNote(updatedNote)
            }

            val action = EditNoteFragmentDirections.actionEditNoteFragmentToNotesListFragment()
            findNavController().navigate(action)
        } else {
            binding.etTitle.error = getString(R.string.title_or_content_required)
        }
    }
}