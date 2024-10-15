package org.bangkit.dicodingevent.data.repository

interface DicodingEventRepository {
    suspend fun getEvents()
    suspend fun getEventDetail(eventId: Int)
    suspend fun searchEvent(query: String)
}