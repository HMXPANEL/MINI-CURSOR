package com.hermes.android.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        SessionEntity::class,
        MessageEntity::class,
        ToolCardEntity::class,
        MemoryEventEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun messageDao(): MessageDao
    abstract fun toolCardDao(): ToolCardDao
    abstract fun memoryEventDao(): MemoryEventDao
}
