package com.hermes.android.ui.chat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hermes.android.domain.model.ChatMessage
import com.hermes.android.domain.model.FinishReason
import com.hermes.android.domain.model.MessageRole
import com.hermes.android.ui.chat.components.*
import com.hermes.android.ui.components.ConnectionStatusChip
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    sessionId: String,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isStreaming by viewModel.isStreaming.collectAsState()
    val connectionState by viewModel.connectionState.collectAsState()
    val uiError by viewModel.uiError.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(messages.size - 1) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hermes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    ConnectionStatusChip(state = connectionState)
                }
            )
        },
        bottomBar = {
            ChatInputBar(
                value = inputText,
                onValueChange = viewModel::onInputChange,
                onSend = viewModel::sendMessage,
                onAttach = { /* TODO: image picker */ },
                onInterrupt = viewModel::interrupt,
                isStreaming = isStreaming,
                enabled = connectionState is com.hermes.android.data.websocket.HermesConnectionManager.State.Ready
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            uiError?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    MessageItem(
                        message = message,
                        onRetry = { viewModel.retryMessage(message.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageItem(
    message: ChatMessage,
    onRetry: () -> Unit
) {
    val isUser = message.role == MessageRole.USER
    val isInterrupted = message.finishReason == FinishReason.INTERRUPTED

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        contentAlignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Card(
            modifier = Modifier
                .animateContentSize()
                .widthIn(max = 340.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            ),
            border = if (isInterrupted) {
                androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.error)
            } else null
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (isUser) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyLarge
                    )
                } else {
                    StreamingText(
                        content = message.content,
                        isStreaming = message.isStreaming
                    )
                }

                if (isInterrupted) {
                    TextButton(onClick = onRetry) {
                        Text("Retry?")
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    onAttach: () -> Unit,
    onInterrupt: () -> Unit,
    isStreaming: Boolean,
    enabled: Boolean
) {
    Surface(
        tonalElevation = 3.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .imePadding(),
            verticalAlignment = Alignment.Bottom
        ) {
            IconButton(onClick = onAttach, enabled = !isStreaming && enabled) {
                Icon(Icons.Default.AttachFile, contentDescription = "Attach image")
            }

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                placeholder = { Text("Message Hermes...") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                enabled = !isStreaming && enabled,
                maxLines = 6
            )

            if (isStreaming) {
                IconButton(onClick = onInterrupt) {
                    Icon(Icons.Default.Stop, contentDescription = "Stop", tint = MaterialTheme.colorScheme.error)
                }
            } else {
                IconButton(
                    onClick = onSend,
                    enabled = value.isNotBlank() && enabled
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}
