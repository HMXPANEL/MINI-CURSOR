package com.hermes.android.domain.model

import android.net.Uri

data class ChatMessage(
    val id: String,
    val sessionId: String,
    val role: MessageRole,
    val content: String,
    val attachments: List<MessageAttachment> = emptyList(),
    val isStreaming: Boolean = false,
    val finishReason: FinishReason? = null,
    val createdAt: Long = System.currentTimeMillis()
)

enum class MessageRole { USER, ASSISTANT }

enum class FinishReason { STOP, INTERRUPTED, ERROR }

data class MessageAttachment(
    val uri: Uri,
    val mimeType: String,
    val base64Data: String? = null
)
