package com.hermes.android.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tool_cards",
    foreignKeys = [
        ForeignKey(
            entity = MessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["messageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("messageId")]
)
data class ToolCardEntity(
    @PrimaryKey val id: String,
    val messageId: String,
    val sessionId: String,
    val toolName: String,
    val argsSummary: String,
    val resultSummary: String? = null,
    val status: String, // "running" | "success" | "error" | "pending_approval"
    val rawArgs: String,
    val rawResult: String? = null,
    val startedAt: Long,
    val completedAt: Long? = null
)
