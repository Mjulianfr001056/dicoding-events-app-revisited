package org.bangkit.dicodingevent.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.bangkit.dicodingevent.data.response.DicodingEventResponseMapper
import org.bangkit.dicodingevent.data.retrofit.DicodingEventApi
import org.bangkit.dicodingevent.util.Result
import javax.inject.Inject

class DicodingEventRepositoryImpl @Inject constructor(
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
                emit(Result.Error(message = e.message ?: "Terjadi kesalahan"))
            }
        }
    }

    override suspend fun getEventDetail(eventId: Int): Result<DicodingEvent> {
        return try {
            val response = api.getDetailEvent(eventId.toString())
            val event = DicodingEventResponseMapper.mapResponseToEntity(response.event)
            Result.Success(event)
        } catch (e: Exception) {
            Log.d(TAG, "getEventDetail: ${e.message}")
            Result.Error(message = e.message ?: "Terjadi kesalahan")
        }
    }

    override suspend fun searchEvent(query: String): Flow<Result<List<DicodingEvent>>> {
        val isActive = -1

        return flow {
            val result = runCatching {
                api.searchEvents(isActive, query)
            }

            result.onSuccess { response ->
                val listEvents = response.listEvents.map {
                    DicodingEventResponseMapper.mapResponseToEntity(it)
                }
                Log.d(TAG, "searchEvent: ${listEvents.size}")
                emit(Result.Success(listEvents))
            }.onFailure { e ->
                Log.d(TAG, "searchEvent: ${e.message}")
                emit(Result.Error(message = e.message ?: "Terjadi kesalahan"))
            }
        }

    }

    companion object {
        private const val TAG = "DicodingEventRepositoryImpl"
    }

}