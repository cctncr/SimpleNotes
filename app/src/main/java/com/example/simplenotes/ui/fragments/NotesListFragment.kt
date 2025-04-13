package com.example.simplenotes.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.Fade
import androidx.transition.TransitionManager
import com.example.simplenotes.R
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

        setupTransitions()
        setupRecyclerView()
        setupListeners()
        setupMenu()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupMenu() {
        val menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.notes_list_menu, menu)
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.queryHint = getString(R.string.search_hint)

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        query?.let { viewModel.onSearchQuerySubmitted(it) }
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        newText?.let { viewModel.onSearchQueryChanged(it) }
                        return true
                    }
                })

                searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        return true
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        viewModel.clearSearch()
                        return true
                    }
                })

                val currentState = viewModel.viewState.value
                if (currentState?.isSearchActive == true) {
                    searchItem.expandActionView()
                    searchView.setQuery(currentState.searchQuery, false)
                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_search -> true
                    else -> false
                }
            }
        }

        requireActivity().addMenuProvider(menuProvider, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun observeViewModel() {
        viewModel.viewState.observe(viewLifecycleOwner) { state ->
            notesAdapter.updateNotes(state.notes)

            (activity as AppCompatActivity).supportActionBar?.title = state.searchTitle

            binding.emptySearchMessage.visibility =
                if (state.isEmptyStateVisible) View.VISIBLE else View.GONE

            binding.notesRecyclerView.visibility =
                if (state.isEmptyStateVisible) View.GONE else View.VISIBLE

            binding.emptySearchMessage.text = state.emptyStateMessage

            binding.fab.visibility = if (state.isFabVisible) View.VISIBLE else View.GONE
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

    private fun setupTransitions() {
        val fadeTransition = Fade()
        fadeTransition.duration = 300
        TransitionManager.beginDelayedTransition(binding.root as ViewGroup, fadeTransition)
        binding.notesRecyclerView.itemAnimator = DefaultItemAnimator()
    }
}