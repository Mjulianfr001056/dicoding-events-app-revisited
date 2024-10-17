package org.bangkit.dicodingevent.data.repository

import kotlinx.coroutines.flow.Flow
import org.bangkit.dicodingevent.data.model.DicodingEventModel
import org.bangkit.dicodingevent.util.Result

interface DicodingEventRepository {
    suspend fun getEvents(isActive: Boolean) : Flow<Result<List<DicodingEventModel>>>
    suspend fun getEventDetail(eventId: Int) : Result<DicodingEventModel>
    suspend fun searchEvent(query: String) : Flow<Result<List<DicodingEventModel>>>
    suspend fun addFavorite(event: DicodingEventModel) : Flow<Result<Boolean>>
    suspend fun removeFavorite(event: DicodingEventModel) : Flow<Result<Boolean>>
    suspend fun findFavorite(eventId: Int) : Result<DicodingEventModel>
}