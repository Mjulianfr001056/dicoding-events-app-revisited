package org.bangkit.dicodingevent.data.response

import org.bangkit.dicodingevent.data.repository.DicodingEvent

class DicodingEventResponseMapper {
    companion object {
        fun mapResponseToEntity(response: ListEventsItem): DicodingEvent {
            return DicodingEvent(
                id = response.id,
                summary = response.summary,
                mediaCover = response.mediaCover,
                registrants = response.registrants,
                imageLogo = response.imageLogo,
                link = response.link,
                description = response.description,
                ownerName = response.ownerName,
                cityName = response.cityName,
                quota = response.quota,
                name = response.name,
                beginTime = response.beginTime,
                endTime = response.endTime,
                category = response.category
            )
        }
    }
}