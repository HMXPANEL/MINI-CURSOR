package com.hermes.android.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MemoryEventDao {
    @Query("SELECT * FROM memory_events WHERE sessionId = :sid ORDER BY createdAt DESC")
    fun observeBySession(sid: String): Flow<List<MemoryEventEntity>>

    @Insert
    suspend fun insert(event: MemoryEventEntity)
}
