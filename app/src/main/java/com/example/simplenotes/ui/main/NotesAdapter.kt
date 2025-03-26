package com.example.simplenotes.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.simplenotes.data.local.entity.Note
import com.example.simplenotes.databinding.NoteItemBinding
import java.util.Collections

class NotesAdapter(
    private var notes: MutableList<Note>,
    private val onNoteDeleted: (Note) -> Unit,
    private val onNotesReordered: (List<Note>) -> Unit
) : RecyclerView.Adapter<NotesAdapter.ViewHolder>(), ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = NoteItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    fun updateNotes(newNotes: List<Note>) {
        val diffResult = DiffUtil.calculateDiff(NoteDiffCallback(notes, newNotes))
        notes = newNotes.toMutableList()
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition..<toPosition) {
                Collections.swap(notes, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(notes, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
        onNotesReordered(notes.toList())
    }

    override fun onItemDismiss(position: Int) {
        val noteToDelete = notes[position]
        onNoteDeleted(noteToDelete)
    }

    override fun onReorderComplete() {
        onNotesReordered(notes.toList())
    }

    inner class ViewHolder(private val binding: NoteItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(note: Note) {
            binding.title.text = note.text
            binding.text.text = note.text
            binding.root.contentDescription = note.title
        }
    }
}