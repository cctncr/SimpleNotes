package com.example.simplenotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotesAdapter(private val notes: List<Note>) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.note_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewGroup = holder.itemView
        viewGroup.findViewById<TextView>(R.id.title).text = notes[position].title
        viewGroup.findViewById<TextView>(R.id.text).text = notes[position].text
        viewGroup.contentDescription = notes[position].title
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}