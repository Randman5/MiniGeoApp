package ru.tz.geo.domain.usecase.mapMarkersUseCases

import com.yandex.mapkit.geometry.Point
import ru.tz.geo.data.database.repository.MarkerDao
import ru.tz.geo.data.mappers.MarkersMapper
import ru.tz.geo.data.yandexMapData.dto.MarkerDto
import javax.inject.Inject

class AddMarkerUseCase @Inject constructor(
    private val markerDao: MarkerDao,
    private val mapper: MarkersMapper
) {
    fun execute (point: Point, text:String) : MarkerDto {
        val entity = mapper.toEntity(point,text)
        markerDao.addMarker(entity)
        return mapper.toDto(entity)
    }
}