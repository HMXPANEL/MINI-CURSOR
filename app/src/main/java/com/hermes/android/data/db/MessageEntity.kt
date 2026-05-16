package com.hermes.android.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val role: String, // "user" | "assistant"
    val content: String,
    val createdAt: Long,
    val finishReason: String? = null, // "stop" | "interrupted" | "error"
    val isStreaming: Boolean = false
)
