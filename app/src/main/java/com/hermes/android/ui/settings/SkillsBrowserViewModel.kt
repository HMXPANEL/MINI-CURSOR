package com.hermes.android.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SkillsBrowserViewModel @Inject constructor() : ViewModel() {

    data class SkillItem(
        val id: String,
        val name: String,
        val description: String,
        val createdAt: Long
    )

    data class SkillsState(
        val skills: List<SkillItem> = emptyList(),
        val isLoading: Boolean = false
    )

    private val _state = MutableStateFlow(SkillsState())
    val state: StateFlow<SkillsState> = _state.asStateFlow()

    init {
        loadSkills()
    }

    private fun loadSkills() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // Stub: in real implementation, call skills_list tool
            _state.value = SkillsState(
                skills = listOf(
                    SkillItem("1", "Code Review", "Automated code review skill", System.currentTimeMillis()),
                    SkillItem("2", "Web Search", "Search and summarize web content", System.currentTimeMillis())
                ),
                isLoading = false
            )
        }
    }

    fun deleteSkill(skillId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                skills = _state.value.skills.filter { it.id != skillId }
            )
        }
    }
}
