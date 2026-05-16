package com.hermes.android.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val createdAt: Long,
    val lastActiveAt: Long,
    val hermesSessionId: String,
    val provider: String,
    val model: String,
    val messageCount: Int = 0,
    val isActive: Boolean = false
)
