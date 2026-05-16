package com.hermes.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.data.websocket.HermesConnectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewerViewModel @Inject constructor(
    private val connectionManager: HermesConnectionManager
) : ViewModel() {

    data class WSFrame(
        val type: String,
        val summary: String,
        val timestamp: String,
        val isError: Boolean = false
    )

    data class LogState(
        val selectedTab: Int = 0,
        val wsFrames: List<WSFrame> = emptyList(),
        val agentLog: List<String> = emptyList()
    )

    private val _state = MutableStateFlow(LogState())
    val state: StateFlow<LogState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            connectionManager.events.collect { event ->
                val frame = WSFrame(
                    type = event::class.simpleName ?: "unknown",
                    summary = event.toString().take(200),
                    timestamp = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault())
                        .format(java.util.Date()),
                    isError = event is com.hermes.android.domain.protocol.HermesInbound.Error
                )
                _state.value = _state.value.copy(
                    wsFrames = _state.value.wsFrames + frame
                )
            }
        }
    }

    fun selectTab(index: Int) {
        _state.value = _state.value.copy(selectedTab = index)
    }
}
