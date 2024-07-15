package ru.tz.geo.domain.usecase.mapMarkersUseCases

import ru.tz.geo.data.database.repository.MarkerDao
import ru.tz.geo.data.mappers.MarkersMapper
import ru.tz.geo.data.yandexMapData.dto.MarkerDto
import javax.inject.Inject

class GetMarkersUseCase @Inject constructor(
    private val markerDao: MarkerDao,
    private val mapper: MarkersMapper
) {
    fun execute(): List<MarkerDto> {
        return markerDao.getMarkers().map(mapper::toDto)
    }
}