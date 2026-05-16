package com.hermes.android.data.repository

import com.hermes.android.data.db.AppDatabase
import com.hermes.android.data.db.MessageEntity
import com.hermes.android.data.db.ToolCardEntity
import com.hermes.android.data.websocket.HermesConnectionManager
import com.hermes.android.domain.model.ChatMessage
import com.hermes.android.domain.model.FinishReason
import com.hermes.android.domain.model.MemoryAction
import com.hermes.android.domain.model.MemoryEvent
import com.hermes.android.domain.model.MessageRole
import com.hermes.android.domain.model.ToolCard
import com.hermes.android.domain.protocol.HermesOutbound
import com.hermes.android.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val db: AppDatabase,
    private val connectionManager: HermesConnectionManager
) : ChatRepository {

    override fun observeMessages(sessionId: String): Flow<List<ChatMessage>> {
        return db.messageDao().observeBySession(sessionId).map { entities ->
            entities.map { e ->
                ChatMessage(
                    id = e.id,
                    sessionId = e.sessionId,
                    role = if (e.role == "user") MessageRole.USER else MessageRole.ASSISTANT,
                    content = e.content,
                    isStreaming = e.isStreaming,
                    finishReason = e.finishReason?.let { reason ->
                        when (reason) {
                            "interrupted" -> FinishReason.INTERRUPTED
                            "error" -> FinishReason.ERROR
                            else -> FinishReason.STOP
                        }
                    },
                    createdAt = e.createdAt
                )
            }
        }
    }

    override fun observeToolCards(messageId: String): Flow<List<ToolCard>> {
        return db.toolCardDao().observeByMessage(messageId).map { entities ->
            entities.map { e ->
                when (e.status) {
                    "running" -> ToolCard.Running(
                        id = e.id, messageId = e.messageId, sessionId = e.sessionId,
                        toolName = e.toolName, argsSummary = e.argsSummary, startedAt = e.startedAt
                    )
                    "success" -> ToolCard.Success(
                        id = e.id, messageId = e.messageId, sessionId = e.sessionId,
                        toolName = e.toolName, argsSummary = e.argsSummary,
                        resultSummary = e.resultSummary ?: "", rawArgs = e.rawArgs,
                        rawResult = e.rawResult ?: "", startedAt = e.startedAt,
                        completedAt = e.completedAt ?: e.startedAt
                    )
                    "error" -> ToolCard.Error(
                        id = e.id, messageId = e.messageId, sessionId = e.sessionId,
                        toolName = e.toolName, argsSummary = e.argsSummary,
                        resultSummary = e.resultSummary ?: "Error", rawArgs = e.rawArgs,
                        rawResult = e.rawResult, startedAt = e.startedAt,
                        completedAt = e.completedAt ?: e.startedAt
                    )
                    "pending_approval" -> ToolCard.PendingApproval(
                        id = e.id, messageId = e.messageId, sessionId = e.sessionId,
                        toolName = e.toolName, argsSummary = e.argsSummary,
                        rawArgs = e.rawArgs, startedAt = e.startedAt
                    )
                    else -> ToolCard.Running(
                        id = e.id, messageId = e.messageId, sessionId = e.sessionId,
                        toolName = e.toolName, argsSummary = e.argsSummary, startedAt = e.startedAt
                    )
                }
            }
        }
    }

    override fun observeMemoryEvents(sessionId: String): Flow<List<MemoryEvent>> {
        return db.memoryEventDao().observeBySession(sessionId).map { entities ->
            entities.map { e ->
                MemoryEvent(
                    id = e.id, sessionId = e.sessionId, messageId = e.messageId,
                    action = when (e.action) {
                        "recalled" -> MemoryAction.RECALLED
                        "updated" -> MemoryAction.UPDATED
                        else -> MemoryAction.SAVED
                    },
                    snippet = e.snippet, createdAt = e.createdAt
                )
            }
        }
    }

    override suspend fun sendMessage(sessionId: String, content: String, attachments: List<String>) {
        val turnId = UUID.randomUUID().toString()
        // Store user message
        db.messageDao().upsert(
            MessageEntity(
                id = turnId, sessionId = sessionId, role = "user",
                content = content, createdAt = System.currentTimeMillis()
            )
        )
        db.sessionDao().touch(sessionId, System.currentTimeMillis())

        // Send to Hermes
        connectionManager.send(
            HermesOutbound.SendMessage(
                turnId = turnId,
                sessionId = sessionId,
                content = content,
                attachments = attachments.map {
                    HermesOutbound.SendMessage.Attachment("image/jpeg", it)
                }
            )
        )
    }

    override suspend fun interruptTurn(turnId: String) {
        connectionManager.send(HermesOutbound.Interrupt(turnId = turnId))
    }

    override suspend fun approveTool(turnId: String, toolCallId: String, approved: Boolean) {
        connectionManager.send(
            HermesOutbound.ToolApproval(turnId = turnId, toolCallId = toolCallId, approved = approved)
        )
    }

    override suspend fun resumeSession(sessionId: String, lastTurnId: String) {
        connectionManager.send(
            HermesOutbound.Resume(sessionId = sessionId, lastTurnId = lastTurnId)
        )
    }
}
