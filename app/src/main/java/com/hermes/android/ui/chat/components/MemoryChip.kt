package com.hermes.android.ui.chat.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hermes.android.domain.model.MemoryEvent

@Composable
fun MemoryChip(
    event: MemoryEvent,
    modifier: Modifier = Modifier
) {
    AssistChip(
        onClick = { },
        label = { Text("${event.action.name.lowercase().replaceFirstChar { it.uppercase() }}: ${event.snippet}") },
        icon = { Icon(Icons.Default.Memory, contentDescription = null) },
        modifier = modifier.padding(horizontal = 16.dp, vertical = 2.dp)
    )
}
