package ru.tz.geo.domain.usecase.mapMarkersUseCases

import com.yandex.mapkit.geometry.Point
import ru.tz.geo.data.database.repository.MarkerDao
import ru.tz.geo.data.mappers.MarkersMapper
import javax.inject.Inject

class DeleteMarkerUseCase @Inject constructor(
    private val markerDao: MarkerDao,
    private val mapper: MarkersMapper
) {
    fun execute (point: Point) {
        markerDao.deleteByPoint(point.latitude,point.longitude)
    }
}