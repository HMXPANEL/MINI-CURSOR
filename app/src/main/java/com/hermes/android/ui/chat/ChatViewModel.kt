package com.hermes.android.ui.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.data.db.AppDatabase
import com.hermes.android.data.db.MessageEntity
import com.hermes.android.data.db.ToolCardEntity
import com.hermes.android.data.websocket.HermesConnectionManager
import com.hermes.android.domain.model.ChatMessage
import com.hermes.android.domain.model.FinishReason
import com.hermes.android.domain.model.MemoryEvent
import com.hermes.android.domain.model.ToolCard
import com.hermes.android.domain.protocol.HermesInbound
import com.hermes.android.domain.usecase.ApproveToolUseCase
import com.hermes.android.domain.usecase.ObserveConnectionStateUseCase
import com.hermes.android.domain.usecase.ObserveMessagesUseCase
import com.hermes.android.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sendMessageUseCase: SendMessageUseCase,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val approveToolUseCase: ApproveToolUseCase,
    private val observeConnectionState: ObserveConnectionStateUseCase,
    private val connectionManager: HermesConnectionManager,
    private val db: AppDatabase
) : ViewModel() {

    private val sessionId: String = savedStateHandle.get<String>("sessionId") ?: "new"

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _pendingApprovals = MutableStateFlow<List<ToolApprovalRequest>>(emptyList())
    val pendingApprovals: StateFlow<List<ToolApprovalRequest>> = _pendingApprovals.asStateFlow()

    private val _uiError = MutableStateFlow<String?>(null)
    val uiError: StateFlow<String?> = _uiError.asStateFlow()

    val connectionState = observeConnectionState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HermesConnectionManager.State.Idle)

    private val deltaBuffer = MutableStateFlow<LiveDelta?>(null)
    private var deltaFlushJob: Job? = null
    private var currentAssistantMessageId: String? = null

    val messages = observeMessagesUseCase(sessionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            connectionManager.events.collect { event ->
                handleInboundEvent(event)
            }
        }
    }

    fun onInputChange(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isEmpty() || _isStreaming.value) return

        viewModelScope.launch {
            _inputText.value = ""
            _isStreaming.value = true
            sendMessageUseCase(sessionId, text)
        }
    }

    fun approveTool(turnId: String, toolCallId: String, approved: Boolean) {
        viewModelScope.launch {
            approveToolUseCase(turnId, toolCallId, approved)
            _pendingApprovals.value = _pendingApprovals.value.filter { it.toolCallId != toolCallId }
        }
    }

    fun interrupt() {
        currentAssistantMessageId?.let { id ->
            viewModelScope.launch {
                connectionManager.send(
                    com.hermes.android.domain.protocol.HermesOutbound.Interrupt(turnId = id)
                )
            }
        }
    }

    fun retryMessage(messageId: String) {
        // Find original user message and resend
        viewModelScope.launch {
            val msg = db.messageDao().observeBySession(sessionId)
                .stateIn(viewModelScope).value.find { it.id == messageId }
            msg?.let {
                _isStreaming.value = true
                sendMessageUseCase(sessionId, it.content)
            }
        }
    }

    private suspend fun handleInboundEvent(event: HermesInbound) {
        when (event) {
            is HermesInbound.Delta -> handleDelta(event)
            is HermesInbound.ToolStart -> handleToolStart(event)
            is HermesInbound.ToolResult -> handleToolResult(event)
            is HermesInbound.TurnDone -> handleTurnDone(event)
            is HermesInbound.MemoryEventMsg -> handleMemoryEvent(event)
            is HermesInbound.Error -> _uiError.value = "${event.code}: ${event.message}"
            else -> {}
        }
    }

    private fun handleDelta(event: HermesInbound.Delta) {
        val buffer = deltaBuffer.value ?: LiveDelta(event.turnId).also {
            currentAssistantMessageId = event.turnId
            // Insert placeholder assistant message
            viewModelScope.launch {
                db.messageDao().upsert(
                    MessageEntity(
                        id = event.turnId,
                        sessionId = sessionId,
                        role = "assistant",
                        content = "",
                        createdAt = System.currentTimeMillis(),
                        isStreaming = true
                    )
                )
            }
        }
        buffer.append(event.text)
        deltaBuffer.value = buffer

        deltaFlushJob?.cancel()
        deltaFlushJob = viewModelScope.launch {
            delay(500)
            flushDelta()
        }
    }

    private suspend fun flushDelta() {
        val buffer = deltaBuffer.value ?: return
        db.messageDao().updateContent(buffer.turnId, buffer.text, true)
    }

    private suspend fun handleToolStart(event: HermesInbound.ToolStart) {
        db.toolCardDao().insert(
            ToolCardEntity(
                id = event.toolCallId,
                messageId = event.turnId,
                sessionId = sessionId,
                toolName = event.toolName,
                argsSummary = event.argsSummary,
                status = "running",
                rawArgs = event.rawArgs.toString(),
                startedAt = System.currentTimeMillis()
            )
        )
    }

    private suspend fun handleToolResult(event: HermesInbound.ToolResult) {
        val status = when (event.status) {
            "approval_required" -> {
                _pendingApprovals.value = _pendingApprovals.value + ToolApprovalRequest(
                    turnId = event.turnId,
                    toolCallId = event.toolCallId,
                    toolName = "", // filled from DB
                    argsSummary = ""
                )
                "pending_approval"
            }
            "error" -> "error"
            else -> "success"
        }
        db.toolCardDao().complete(
            id = event.toolCallId,
            status = status,
            summary = event.resultSummary,
            raw = event.rawResult.toString(),
            ts = System.currentTimeMillis()
        )
    }

    private suspend fun handleTurnDone(event: HermesInbound.TurnDone) {
        deltaFlushJob?.cancel()
        flushDelta()
        deltaBuffer.value = null
        _isStreaming.value = false
        currentAssistantMessageId = null

        val reason = when (event.finishReason) {
            "interrupted" -> "interrupted"
            "error" -> "error"
            else -> "stop"
        }
        db.messageDao().finalize(event.turnId, reason)
    }

    private suspend fun handleMemoryEvent(event: HermesInbound.MemoryEventMsg) {
        db.memoryEventDao().insert(
            com.hermes.android.data.db.MemoryEventEntity(
                id = UUID.randomUUID().toString(),
                sessionId = sessionId,
                messageId = event.turnId,
                action = event.action,
                snippet = event.snippet,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    data class ToolApprovalRequest(
        val turnId: String,
        val toolCallId: String,
        val toolName: String,
        val argsSummary: String
    )

    data class LiveDelta(
        val turnId: String,
        val text: String = ""
    ) {
        fun append(newText: String): LiveDelta = copy(text = text + newText)
    }
}
