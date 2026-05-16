package com.hermes.android.domain.protocol

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
sealed class HermesInbound {
    abstract val type: String

    @Serializable
    data class Delta(
        val turnId: String,
        val text: String,
        override val type: String = "delta"
    ) : HermesInbound()

    @Serializable
    data class ToolStart(
        val turnId: String,
        val toolCallId: String,
        val toolName: String,
        val argsSummary: String,
        val rawArgs: JsonElement,
        override val type: String = "tool_start"
    ) : HermesInbound()

    @Serializable
    data class ToolResult(
        val turnId: String,
        val toolCallId: String,
        val status: String, // success | error | approval_required
        val resultSummary: String,
        val rawResult: JsonElement,
        override val type: String = "tool_result"
    ) : HermesInbound()

    @Serializable
    data class TurnDone(
        val turnId: String,
        val finishReason: String, // stop | interrupted | error
        override val type: String = "turn_done"
    ) : HermesInbound()

    @Serializable
    data class MemoryEventMsg(
        val turnId: String,
        val action: String, // saved | recalled | updated
        val snippet: String,
        override val type: String = "memory_event"
    ) : HermesInbound()

    @Serializable
    data class Pong(
        val ts: Long,
        override val type: String = "pong"
    ) : HermesInbound()

    @Serializable
    data class Error(
        val code: String,
        val message: String,
        override val type: String = "error"
    ) : HermesInbound()
}
