package org.bangkit.dicodingevent.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DicodingEventEntity::class],
    version = 1
)
abstract class DicodingEventDatabase : RoomDatabase() {

    abstract val dicodingEventDao: DicodingEventDao

}