package com.hermes.android.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE sessionId = :sid ORDER BY createdAt ASC")
    fun observeBySession(sid: String): Flow<List<MessageEntity>>

    @Upsert
    suspend fun upsert(message: MessageEntity)

    @Query("UPDATE messages SET content = :content, isStreaming = :streaming WHERE id = :id")
    suspend fun updateContent(id: String, content: String, streaming: Boolean)

    @Query("UPDATE messages SET isStreaming = 0, finishReason = :reason WHERE id = :id")
    suspend fun finalize(id: String, reason: String)

    @Query("DELETE FROM messages WHERE sessionId = :sid")
    suspend fun deleteBySession(sid: String)
}
