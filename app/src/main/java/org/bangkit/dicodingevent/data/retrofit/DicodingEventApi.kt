package org.bangkit.dicodingevent.data.retrofit

import org.bangkit.dicodingevent.data.response.DicodingEventResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DicodingEventApi {
    @GET("events")
    suspend fun getEvents(
        @Query("active") active: Int
    ): DicodingEventResponse

    @GET("events/{id}")
    suspend fun getDetailEvent(
        @Path("id") id: String
    ): DicodingEventResponse

    @GET("events")
    fun searchEvents(
        @Query("active") active: Int,
        @Query("q") query: String
    ): DicodingEventResponse
}