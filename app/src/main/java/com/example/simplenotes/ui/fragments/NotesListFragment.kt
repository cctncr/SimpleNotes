package com.example.simplenotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simplenotes.data.local.entity.Note
import com.example.simplenotes.databinding.FragmentNotesListBinding
import com.example.simplenotes.ui.main.NoteTouchHelperCallback
import com.example.simplenotes.ui.main.NotesAdapter
import com.example.simplenotes.viewmodel.NoteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotesListFragment : Fragment() {
    private var _binding: FragmentNotesListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NoteViewModel by viewModels()
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupListeners()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun observeViewModel() {
        viewModel.allNotes.observe(viewLifecycleOwner) { notes ->
            notesAdapter.updateNotes(notes)
        }
    }

    private fun setupListeners() {
        binding.fab.setOnClickListener {
            val action = NotesListFragmentDirections.actionNotesListFragmentToAddNoteFragment()
            findNavController().navigate(action)
        }
    }

    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(
            emptyList<Note>().toMutableList(),
            onNoteDeleted = { deletedNote ->
                viewModel.deleteNote(deletedNote)
            },
            onNotesReordered = { reorderedNotes ->
                viewModel.reorderNotes(reorderedNotes)
            },
            onNoteClick = { clickedNote ->
                val action = NotesListFragmentDirections.actionNotesListFragmentToEditNoteFragment(
                    clickedNote.id
                )
                findNavController().navigate(action)
            }
        )

        binding.notesRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = notesAdapter

            val callback = NoteTouchHelperCallback(notesAdapter)
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(this)
        }
    }
}