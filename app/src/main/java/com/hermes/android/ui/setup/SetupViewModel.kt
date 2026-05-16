package com.hermes.android.ui.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.data.keystore.ConfigStore
import com.hermes.android.data.termux.TermuxLauncher
import com.hermes.android.data.websocket.HermesConnectionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val termuxLauncher: TermuxLauncher,
    private val configStore: ConfigStore,
    private val connectionManager: HermesConnectionManager
) : ViewModel() {

    data class SetupState(
        val isTermuxInstalled: Boolean = false,
        val isPermissionGranted: Boolean = false,
        val isHermesInstalled: Boolean = false,
        val apiKey: String = "",
        val provider: String = "openai",
        val connectionState: HermesConnectionManager.State = HermesConnectionManager.State.Idle,
        val error: String? = null
    )

    private val _state = MutableStateFlow(SetupState())
    val state: StateFlow<SetupState> = _state.asStateFlow()

    init {
        checkPrerequisites()
    }

    private fun checkPrerequisites() {
        _state.value = _state.value.copy(
            isTermuxInstalled = termuxLauncher.isTermuxInstalled(),
            apiKey = configStore.getApiKey(),
            provider = configStore.providerValue
        )
    }

    fun checkHermesInstalled() {
        // Attempt to launch hermes --version via termux
        viewModelScope.launch {
            try {
                termuxLauncher.launch()
                _state.value = _state.value.copy(isHermesInstalled = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun saveApiKey(provider: String, key: String) {
        configStore.providerValue = provider
        configStore.setApiKey(key)
        _state.value = _state.value.copy(apiKey = key, provider = provider)
    }

    fun startConnection() {
        connectionManager.start(viewModelScope)
    }
}
