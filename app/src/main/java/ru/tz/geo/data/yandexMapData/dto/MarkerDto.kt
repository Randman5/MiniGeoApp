package ru.tz.geo.data.yandexMapData.dto

import com.yandex.mapkit.geometry.Point

data class MarkerDto(
    val id: Int?,
    val point: Point,
    val text: String
)
