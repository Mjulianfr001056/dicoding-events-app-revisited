package org.bangkit.dicodingevent.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DicodingEvent::class],
    version = 1
)
abstract class DicodingEventDatabase : RoomDatabase() {

    abstract fun dicodingEventDao(): DicodingEventDao

}