package com.example.simplenotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.simplenotes.R
import com.example.simplenotes.databinding.FragmentAddNoteBinding
import com.example.simplenotes.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNoteFragment : Fragment() {
    private var _binding: FragmentAddNoteBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupListeners() {
        binding.fabSave.setOnClickListener {
            saveNote()
        }
    }

    private fun saveNote() {
        val title = binding.etTitle.text.toString()
        val text = binding.etText.text.toString()

        if (viewModel.createAndSaveNote(title, text)) {
            val action = AddNoteFragmentDirections.actionAddNoteFragmentToNotesListFragment()
            findNavController().navigate(action)
        } else {
            binding.etTitle.error = getString(R.string.title_or_content_required)
        }
    }
}