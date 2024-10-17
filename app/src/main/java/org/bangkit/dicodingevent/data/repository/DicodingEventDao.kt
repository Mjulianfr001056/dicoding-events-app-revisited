package org.bangkit.dicodingevent.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DicodingEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDicodingEvent(dicodingEventEntity: DicodingEventEntity)

    @Query("SELECT * FROM dicoding_event ORDER BY created_at ASC")
    suspend fun getAllDicodingEvent(): List<DicodingEventEntity>

    @Delete
    suspend fun deleteDicodingEvent(dicodingEventEntity: DicodingEventEntity)
}