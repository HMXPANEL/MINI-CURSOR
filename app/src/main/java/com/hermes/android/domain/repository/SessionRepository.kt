package com.hermes.android.domain.repository

import com.hermes.android.domain.model.Session
import kotlinx.coroutines.flow.Flow

interface SessionRepository {
    fun observeAllSessions(): Flow<List<Session>>
    suspend fun createSession(title: String, provider: String, model: String): Session
    suspend fun deleteSession(sessionId: String)
    suspend fun updateSession(session: Session)
    suspend fun getSession(sessionId: String): Session?
}
