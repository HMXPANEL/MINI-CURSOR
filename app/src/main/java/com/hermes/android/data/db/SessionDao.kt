package com.hermes.android.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY lastActiveAt DESC")
    fun observeAll(): Flow<List<SessionEntity>>

    @Upsert
    suspend fun upsert(session: SessionEntity)

    @Query("UPDATE sessions SET lastActiveAt = :ts, messageCount = messageCount + 1 WHERE id = :id")
    suspend fun touch(id: String, ts: Long)

    @Query("DELETE FROM sessions WHERE id = :id")
    suspend fun delete(id: String)

    @Query("SELECT * FROM sessions WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): SessionEntity?
}
