package com.hermes.android.data.repository

import com.hermes.android.data.db.AppDatabase
import com.hermes.android.data.db.SessionEntity
import com.hermes.android.domain.model.Session
import com.hermes.android.domain.repository.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val db: AppDatabase
) : SessionRepository {

    override fun observeAllSessions(): Flow<List<Session>> {
        return db.sessionDao().observeAll().map { entities ->
            entities.map { e ->
                Session(
                    id = e.id, title = e.title, createdAt = e.createdAt,
                    lastActiveAt = e.lastActiveAt, hermesSessionId = e.hermesSessionId,
                    provider = e.provider, model = e.model, messageCount = e.messageCount,
                    isActive = e.isActive
                )
            }
        }
    }

    override suspend fun createSession(title: String, provider: String, model: String): Session {
        val id = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val session = SessionEntity(
            id = id, title = title, createdAt = now, lastActiveAt = now,
            hermesSessionId = id, provider = provider, model = model
        )
        db.sessionDao().upsert(session)
        return Session(
            id = id, title = title, createdAt = now, lastActiveAt = now,
            hermesSessionId = id, provider = provider, model = model
        )
    }

    override suspend fun deleteSession(sessionId: String) {
        db.sessionDao().delete(sessionId)
    }

    override suspend fun updateSession(session: Session) {
        db.sessionDao().upsert(
            SessionEntity(
                id = session.id, title = session.title, createdAt = session.createdAt,
                lastActiveAt = session.lastActiveAt, hermesSessionId = session.hermesSessionId,
                provider = session.provider, model = session.model,
                messageCount = session.messageCount, isActive = session.isActive
            )
        )
    }

    override suspend fun getSession(sessionId: String): Session? {
        return db.sessionDao().getById(sessionId)?.let { e ->
            Session(
                id = e.id, title = e.title, createdAt = e.createdAt,
                lastActiveAt = e.lastActiveAt, hermesSessionId = e.hermesSessionId,
                provider = e.provider, model = e.model, messageCount = e.messageCount,
                isActive = e.isActive
            )
        }
    }
}
