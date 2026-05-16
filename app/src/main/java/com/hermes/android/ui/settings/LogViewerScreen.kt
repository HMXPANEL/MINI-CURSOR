package com.hermes.android.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogViewerScreen(
    viewModel: LogViewerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.wsFrames.size) {
        if (state.wsFrames.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(state.wsFrames.size - 1) }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Logs") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = state.selectedTab) {
                Tab(
                    selected = state.selectedTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    text = { Text("WS Frames") }
                )
                Tab(
                    selected = state.selectedTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    text = { Text("Agent Log") }
                )
            }

            when (state.selectedTab) {
                0 -> {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        items(state.wsFrames) { frame ->
                            WSFrameItem(frame = frame)
                        }
                    }
                }
                1 -> {
                    SelectionContainer {
                        Text(
                            text = state.agentLog.joinToString("
"),
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                                .verticalScroll(rememberScrollState())
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WSFrameItem(frame: LogViewerViewModel.WSFrame) {
    val color = when (frame.type) {
        "delta" -> Color.Gray
        "tool_start" -> Color(0xFFFFB300)
        "tool_result" -> if (frame.isError) Color(0xFFE53935) else Color(0xFF4CAF50)
        "error" -> Color(0xFFE53935)
        else -> Color.Gray
    }

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "[${frame.timestamp}] ${frame.type}: ${frame.summary}",
            style = MaterialTheme.typography.bodySmall,
            color = color,
            modifier = Modifier.padding(8.dp)
        )
    }
}
