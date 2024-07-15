package ru.tz.geo.utils

import com.yandex.mapkit.geometry.Point

fun Point.equal(point: Point) = this.latitude == point.latitude && this.longitude == point.longitude