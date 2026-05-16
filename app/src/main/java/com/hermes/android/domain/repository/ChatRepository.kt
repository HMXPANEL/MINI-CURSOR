package com.hermes.android.domain.repository

import com.hermes.android.domain.model.ChatMessage
import com.hermes.android.domain.model.MemoryEvent
import com.hermes.android.domain.model.ToolCard
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessages(sessionId: String): Flow<List<ChatMessage>>
    fun observeToolCards(messageId: String): Flow<List<ToolCard>>
    fun observeMemoryEvents(sessionId: String): Flow<List<MemoryEvent>>
    suspend fun sendMessage(sessionId: String, content: String, attachments: List<String> = emptyList())
    suspend fun interruptTurn(turnId: String)
    suspend fun approveTool(turnId: String, toolCallId: String, approved: Boolean)
    suspend fun resumeSession(sessionId: String, lastTurnId: String)
}
