package com.example.simplenotes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    val title: String,
    val text: String,
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}