package org.bangkit.dicodingevent.data.repository

import kotlinx.coroutines.flow.Flow
import org.bangkit.dicodingevent.data.retrofit.DicodingEventApi
import org.bangkit.dicodingevent.util.Result
import javax.inject.Inject

class DicodingEventRepositoryImpl @Inject constructor(
    private val dao : DicodingEventDao,
    private val api : DicodingEventApi
) : DicodingEventRepository {
    override suspend fun getEvents(): Flow<Result<List<DicodingEvent>>> {
        TODO("Not yet implemented")
    }

    override suspend fun getEventDetail(eventId: Int): Result<DicodingEvent> {
        TODO("Not yet implemented")
    }

    override suspend fun searchEvent(query: String): Result<List<DicodingEvent>> {
        TODO("Not yet implemented")
    }

}