package com.hermes.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.domain.usecase.SendMessageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfigEditorViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase
) : ViewModel() {

    data class EditorState(
        val content: String = "",
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state.asStateFlow()

    init {
        loadConfig()
    }

    private fun loadConfig() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // In real implementation, use read_file tool via sendMessageUseCase
            _state.value = _state.value.copy(
                content = "# Hermes config\nprovider: openai\nmodel: gpt-4o\n",
                isLoading = false
            )
        }
    }

    fun onContentChange(newContent: String) {
        _state.value = _state.value.copy(content = newContent)
    }

    fun saveConfig() {
        viewModelScope.launch {
            // In real implementation, use write_file tool
            _state.value = _state.value.copy(error = "Saved (stub)")
        }
    }
}
