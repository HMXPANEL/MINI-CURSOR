package com.hermes.android.ui.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hermes.android.domain.model.Session
import com.hermes.android.domain.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SessionListViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    val sessions: StateFlow<List<Session>> = sessionRepository.observeAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun createSession(title: String, provider: String, model: String) {
        viewModelScope.launch {
            sessionRepository.createSession(title, provider, model)
        }
    }

    fun deleteSession(sessionId: String) {
        viewModelScope.launch {
            sessionRepository.deleteSession(sessionId)
        }
    }
}
