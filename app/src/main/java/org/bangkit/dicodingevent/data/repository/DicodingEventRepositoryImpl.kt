package org.bangkit.dicodingevent.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.bangkit.dicodingevent.data.response.DicodingEventResponseMapper
import org.bangkit.dicodingevent.data.retrofit.DicodingEventApi
import org.bangkit.dicodingevent.util.Result

class DicodingEventRepositoryImpl(
    private val dao : DicodingEventDao,
    private val api : DicodingEventApi
) : DicodingEventRepository {
    override suspend fun getEvents(isActive: Boolean): Flow<Result<List<DicodingEvent>>> {
        val query = if (isActive) 1 else 0

        return flow {
            val result = runCatching {
                api.getEvents(query)
            }

            result.onSuccess { response ->
                val listEvents = response.listEvents.map {
                    DicodingEventResponseMapper.mapResponseToEntity(it)
                }
                emit(Result.Success(listEvents))
            }.onFailure { e ->
                Log.d(TAG, "getEvents: ${e.message}")
                emit(Result.Error(message = e.message ?: "Unknown error occurred"))
            }
        }
    }

    override suspend fun getEventDetail(eventId: Int): Result<DicodingEvent> {
        TODO("Not yet implemented")
    }

    override suspend fun searchEvent(query: String): Result<List<DicodingEvent>> {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "DicodingEventRepositoryImpl"
    }

}