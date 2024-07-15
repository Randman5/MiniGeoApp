package ru.tz.geo.data.mappers

import com.yandex.mapkit.geometry.Point
import ru.tz.geo.data.database.entity.MarkerEntity
import ru.tz.geo.data.yandexMapData.dto.MarkerDto

class MarkersMapper {

    fun toEntity(markerDto: MarkerDto):MarkerEntity {
        return MarkerEntity(
            id = markerDto.id ?: 0,
            latitude = markerDto.point.latitude,
            longitude = markerDto.point.longitude,
            text = markerDto.text
        )
    }

    fun toEntity(point: Point, text:String): MarkerEntity {
        return MarkerEntity(
            id = 0,
            latitude = point.latitude,
            longitude = point.longitude,
            text = text
        )
    }

    fun toDto(markerEntity: MarkerEntity): MarkerDto {
        return MarkerDto(
            id = markerEntity.id,
            point = Point(
                markerEntity.latitude,
                markerEntity.longitude
            ),
            text = markerEntity.text
        )
    }
}