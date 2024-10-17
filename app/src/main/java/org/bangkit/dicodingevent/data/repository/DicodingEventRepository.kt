package org.bangkit.dicodingevent.data.repository

import kotlinx.coroutines.flow.Flow
import org.bangkit.dicodingevent.util.Result

interface DicodingEventRepository {
    suspend fun getEvents(isActive: Boolean) : Flow<Result<List<DicodingEvent>>>
    suspend fun getEventDetail(eventId: Int) : Result<DicodingEvent>
    suspend fun searchEvent(query: String) : Flow<Result<List<DicodingEvent>>>
}