package org.bangkit.dicodingevent.data.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DicodingEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteEvent(dicodingEventEntity: DicodingEventEntity)

    @Query("SELECT * FROM dicoding_event ORDER BY created_at ASC")
    suspend fun getAllFavoriteEvents(): List<DicodingEventEntity>

    @Delete
    suspend fun deleteFavoriteEvent(dicodingEventEntity: DicodingEventEntity)

    @Query("SELECT * FROM dicoding_event WHERE id = :eventId LIMIT 1")
    suspend fun findFavoriteEvent(eventId: Int): DicodingEventEntity?
}