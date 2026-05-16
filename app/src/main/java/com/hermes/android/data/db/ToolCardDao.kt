package com.hermes.android.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ToolCardDao {
    @Query("SELECT * FROM tool_cards WHERE messageId = :mid ORDER BY startedAt ASC")
    fun observeByMessage(mid: String): Flow<List<ToolCardEntity>>

    @Insert
    suspend fun insert(card: ToolCardEntity)

    @Query("""
        UPDATE tool_cards 
        SET status = :status, resultSummary = :summary, rawResult = :raw, completedAt = :ts 
        WHERE id = :id
    """)
    suspend fun complete(id: String, status: String, summary: String, raw: String, ts: Long)

    @Query("UPDATE tool_cards SET status = 'error', resultSummary = :summary, completedAt = :ts WHERE id = :id")
    suspend fun markError(id: String, summary: String, ts: Long)
}
