package org.bangkit.dicodingevent.data.repository

import kotlinx.coroutines.flow.Flow
import org.bangkit.dicodingevent.util.Result

interface DicodingEventRepository {
    suspend fun getEvents() : Flow<Result<List<DicodingEvent>>>
    suspend fun getEventDetail(eventId: Int) : Result<DicodingEvent>
    suspend fun searchEvent(query: String) : Result<List<DicodingEvent>>
}