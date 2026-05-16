package com.hermes.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.data.keystore.ConfigStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val configStore: ConfigStore
) : ViewModel() {

    data class SettingsState(
        val provider: String = "openai",
        val model: String = "gpt-4o",
        val apiKey: String = "",
        val autoApprove: Boolean = false,
        val githubPat: String = ""
    )

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        _state.value = SettingsState(
            provider = configStore.providerValue,
            model = configStore.modelValue,
            apiKey = configStore.getApiKey(),
            autoApprove = configStore.autoApproveValue,
            githubPat = configStore.getGithubPat()
        )
    }

    fun setProvider(value: String) { _state.value = _state.value.copy(provider = value) }
    fun setModel(value: String) { _state.value = _state.value.copy(model = value) }
    fun setApiKey(value: String) { _state.value = _state.value.copy(apiKey = value) }
    fun setAutoApprove(value: Boolean) { _state.value = _state.value.copy(autoApprove = value) }
    fun setGithubPat(value: String) { _state.value = _state.value.copy(githubPat = value) }

    fun saveSettings() {
        viewModelScope.launch {
            configStore.providerValue = _state.value.provider
            configStore.modelValue = _state.value.model
            configStore.setApiKey(_state.value.apiKey)
            configStore.autoApproveValue = _state.value.autoApprove
            configStore.setGithubPat(_state.value.githubPat)
        }
    }
}
