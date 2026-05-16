package com.hermes.android.ui.sessions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.hermes.android.domain.model.Session
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionListScreen(
    onSessionClick: (String) -> Unit,
    onNewSession: () -> Unit,
    viewModel: SessionListViewModel = hiltViewModel()
) {
    val sessions by viewModel.sessions.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sessions") },
                actions = {
                    IconButton(onClick = onNewSession) {
                        Icon(Icons.Default.Add, contentDescription = "New session")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sessions, key = { it.id }) { session ->
                SessionItem(
                    session = session,
                    onClick = { onSessionClick(session.id) },
                    onDelete = { viewModel.deleteSession(session.id) }
                )
            }
        }
    }
}

@Composable
private fun SessionItem(
    session: Session,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault()) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${session.provider} · ${session.model} · ${dateFormat.format(Date(session.lastActiveAt))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
