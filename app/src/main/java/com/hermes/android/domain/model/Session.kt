package com.hermes.android.domain.model

data class Session(
    val id: String,
    val title: String,
    val createdAt: Long,
    val lastActiveAt: Long,
    val hermesSessionId: String,
    val provider: String,
    val model: String,
    val messageCount: Int = 0,
    val isActive: Boolean = false
)
