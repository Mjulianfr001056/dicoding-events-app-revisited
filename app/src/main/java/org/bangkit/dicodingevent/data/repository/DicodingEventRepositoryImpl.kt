package org.bangkit.dicodingevent.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.bangkit.dicodingevent.data.model.DicodingEventModel
import org.bangkit.dicodingevent.data.retrofit.DicodingEventApi
import org.bangkit.dicodingevent.util.Result
import javax.inject.Inject

class DicodingEventRepositoryImpl @Inject constructor(
    private val dao : DicodingEventDao,
    private val api : DicodingEventApi
) : DicodingEventRepository {
    override suspend fun getEvents(isActive: Boolean): Flow<Result<List<DicodingEventModel>>> {
        val query = if (isActive) 1 else 0

        return flow {
            val result = runCatching {
                api.getEvents(query)
            }

            result.onSuccess { response ->
                val listEvents = response.listEvents.map {
                    it.toModel()
                }
                emit(Result.Success(listEvents))
            }.onFailure { e ->
                Log.d(TAG, "getEvents: ${e.message}")
                emit(Result.Error(message = e.message ?: "Terjadi kesalahan"))
            }
        }
    }

    override suspend fun getEventDetail(eventId: Int): Result<DicodingEventModel> {
        return try {
            val response = api.getDetailEvent(eventId.toString())
            val event = response.event.toModel()

            Result.Success(event)
        } catch (e: Exception) {
            Log.d(TAG, "getEventDetail: ${e.message}")
            Result.Error(message = e.message ?: "Terjadi kesalahan")
        }
    }

    override suspend fun searchEvent(query: String): Flow<Result<List<DicodingEventModel>>> {
        val isActive = -1

        return flow {
            val result = runCatching {
                api.searchEvents(isActive, query)
            }

            result.onSuccess { response ->
                val listEvents = response.listEvents.map {
                    it.toModel()
                }
                Log.d(TAG, "searchEvent: ${listEvents.size}")
                emit(Result.Success(listEvents))
            }.onFailure { e ->
                Log.d(TAG, "searchEvent: ${e.message}")
                emit(Result.Error(message = e.message ?: "Terjadi kesalahan"))
            }
        }
    }

    override suspend fun addFavorite(event: DicodingEventModel): Flow<Result<Boolean>> {
        val eventEntity = event.toEntity()
        return flow {
            try {
                dao.addFavoriteEvent(eventEntity)
                emit(Result.Success(true))
            } catch (e: Exception) {
                Log.d(TAG, "addFavorite: ${e.message}")
                emit(Result.Error(message = e.message ?: "Terjadi kesalahan"))
            }
        }
    }

    override suspend fun removeFavorite(event: DicodingEventModel): Flow<Result<Boolean>> {
        val eventEntity = event.toEntity()
        return flow {
            try {
                dao.deleteFavoriteEvent(eventEntity)
                emit(Result.Success(true))
            } catch (e: Exception) {
                Log.d(TAG, "removeFavorite: ${e.message}")
                emit(Result.Error(message = e.message ?: "Terjadi kesalahan"))
            }
        }
    }

    override suspend fun findFavorite(eventId: Int): Result<DicodingEventModel> {
        return try {
            val eventEntity = dao.findFavoriteEvent(eventId)
            val event = eventEntity?.toModel()

            if (event != null) {
                Result.Success(event)
            } else {
                Result.Error(message = "Event tidak ditemukan")
            }
        } catch (e: Exception) {
            Log.d(TAG, "findFavorite: ${e.message}")
            Result.Error(message = e.message ?: "Terjadi kesalahan")
        }
    }

    override suspend fun getAllFavoriteEvents(): Flow<Result<List<DicodingEventModel>>> {
        return flow {
            try {
                val listEvents = dao.getAllFavoriteEvents().map {
                    it.toModel()
                }
                emit(Result.Success(listEvents))
            } catch (e: Exception) {
                Log.d(TAG, "getAllFavoriteEvents: ${e.message}")
                emit(Result.Error(message = e.message ?: "Terjadi kesalahan"))
            }
        }
    }

    override suspend fun getClosestEvent(): Result<DicodingEventModel> {
        return try {
            val eventEntity = api.getClosestEvents()
            val event = eventEntity.listEvents.first().toModel()

            Result.Success(event)
        } catch (e: Exception) {
            Log.d(TAG, "getClosestEvent: ${e.message}")
            Result.Error(message = e.message ?: "Terjadi kesalahan")
        }
    }

    companion object {
        private const val TAG = "DicodingEventRepositoryImpl"
    }

}