package com.hermes.android.domain.model

sealed class ToolCard(
    open val id: String,
    open val messageId: String,
    open val sessionId: String,
    open val toolName: String,
    open val argsSummary: String,
    open val startedAt: Long
) {
    data class Running(
        override val id: String,
        override val messageId: String,
        override val sessionId: String,
        override val toolName: String,
        override val argsSummary: String,
        override val startedAt: Long
    ) : ToolCard(id, messageId, sessionId, toolName, argsSummary, startedAt)

    data class Success(
        override val id: String,
        override val messageId: String,
        override val sessionId: String,
        override val toolName: String,
        override val argsSummary: String,
        val resultSummary: String,
        val rawArgs: String,
        val rawResult: String,
        override val startedAt: Long,
        val completedAt: Long
    ) : ToolCard(id, messageId, sessionId, toolName, argsSummary, startedAt)

    data class Error(
        override val id: String,
        override val messageId: String,
        override val sessionId: String,
        override val toolName: String,
        override val argsSummary: String,
        val resultSummary: String,
        val rawArgs: String,
        val rawResult: String?,
        override val startedAt: Long,
        val completedAt: Long
    ) : ToolCard(id, messageId, sessionId, toolName, argsSummary, startedAt)

    data class PendingApproval(
        override val id: String,
        override val messageId: String,
        override val sessionId: String,
        override val toolName: String,
        override val argsSummary: String,
        val rawArgs: String,
        override val startedAt: Long
    ) : ToolCard(id, messageId, sessionId, toolName, argsSummary, startedAt)
}
