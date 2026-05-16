package com.hermes.android.domain.model

data class MemoryEvent(
    val id: String,
    val sessionId: String,
    val messageId: String,
    val action: MemoryAction,
    val snippet: String,
    val createdAt: Long
)

enum class MemoryAction { SAVED, RECALLED, UPDATED }
