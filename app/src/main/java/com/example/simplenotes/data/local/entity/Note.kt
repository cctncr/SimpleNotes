package com.example.simplenotes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Note(
    val title: String,
    val text: String,
    var position: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}