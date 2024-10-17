package org.bangkit.dicodingevent.data.repository

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bangkit.dicodingevent.data.model.DicodingEventModel

@Entity(tableName = "dicoding_event")
data class DicodingEventEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int? = null,
    val name: String? = null,
    val mediaCover: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toModel() = DicodingEventModel(
        id = id,
        name = name,
        mediaCover = mediaCover
    )
}