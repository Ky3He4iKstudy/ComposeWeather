package dev.ky3he4ik.composeweather.model

import dev.ky3he4ik.composeweather.domain.model.AstroDataDomainObject

data class Days(
    val dayOfWeek: String,
    val date: String,
    val day: DayDomainObject,
    val hours: List<Hours>,
    val astroData: AstroDataDomainObject
)
