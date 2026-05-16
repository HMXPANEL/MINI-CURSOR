package com.hermes.android.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "memory_events")
data class MemoryEventEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val messageId: String,
    val action: String, // "saved" | "recalled" | "updated"
    val snippet: String,
    val createdAt: Long
)
