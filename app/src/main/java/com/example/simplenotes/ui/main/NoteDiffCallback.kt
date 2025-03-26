package com.example.simplenotes.ui.main

import androidx.recyclerview.widget.DiffUtil
import com.example.simplenotes.data.local.entity.Note

class NoteDiffCallback(
    private val oldList: List<Note>,
    private val newList: List<Note>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldId = oldList.getOrNull(oldItemPosition)?.id ?: return false
        val newId = newList.getOrNull(newItemPosition)?.id ?: return false
        return oldId == newId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].title == newList[newItemPosition].title &&
                oldList[oldItemPosition].text == newList[newItemPosition].text
    }
}