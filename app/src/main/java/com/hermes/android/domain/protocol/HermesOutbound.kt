package com.hermes.android.domain.protocol

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
sealed class HermesOutbound {
    abstract val type: String

    @Serializable
    data class Configure(
        val provider: String,
        val model: String,
        val apiKey: String,
        val autoApprove: Boolean = false,
        override val type: String = "configure"
    ) : HermesOutbound()

    @Serializable
    data class SendMessage(
        val turnId: String,
        val sessionId: String,
        val content: String,
        val attachments: List<Attachment> = emptyList(),
        override val type: String = "send_message"
    ) : HermesOutbound() {
        @Serializable
        data class Attachment(val mimeType: String, val data: String)
    }

    @Serializable
    data class Interrupt(
        val turnId: String,
        override val type: String = "interrupt"
    ) : HermesOutbound()

    @Serializable
    data class Resume(
        val sessionId: String,
        val lastTurnId: String,
        override val type: String = "resume"
    ) : HermesOutbound()

    @Serializable
    data class ToolApproval(
        val turnId: String,
        val toolCallId: String,
        val approved: Boolean,
        override val type: String = "tool_approval"
    ) : HermesOutbound()

    @Serializable
    data class Ping(
        val ts: Long = System.currentTimeMillis(),
        override val type: String = "ping"
    ) : HermesOutbound()

    @Serializable
    data class Pause(
        override val type: String = "pause"
    ) : HermesOutbound()
}
