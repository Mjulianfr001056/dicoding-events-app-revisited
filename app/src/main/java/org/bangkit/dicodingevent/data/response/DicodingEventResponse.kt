package org.bangkit.dicodingevent.data.response

import org.bangkit.dicodingevent.data.model.DicodingEventModel

data class DicodingEventResponse(
	val error: Boolean? = null,
	val message: String? = null,
	val listEvents: List<DicodingEventDto> = listOf()
)

data class DetailDicodingEventResponse(
	val error: Boolean? = null,
	val message: String? = null,
	val event: DicodingEventDto
)

data class DicodingEventDto(
	val summary: String? = null,
	val mediaCover: String? = null,
	val registrants: Int? = null,
	val imageLogo: String? = null,
	val link: String? = null,
	val description: String? = null,
	val ownerName: String? = null,
	val cityName: String? = null,
	val quota: Int? = null,
	val name: String? = null,
	val id: Int? = null,
	val beginTime: String? = null,
	val endTime: String? = null,
	val category: String? = null
) {
	fun toModel() = DicodingEventModel(
		summary = summary,
		mediaCover = mediaCover,
		registrants = registrants,
		imageLogo = imageLogo,
		link = link,
		description = description,
		ownerName = ownerName,
		cityName = cityName,
		quota = quota,
		name = name,
		id = id,
		beginTime = beginTime,
		endTime = endTime,
		category = category
	)
}