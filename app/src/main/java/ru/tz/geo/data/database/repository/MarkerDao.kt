package ru.tz.geo.data.database.repository

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ru.tz.geo.data.database.entity.MarkerEntity

@Dao
interface MarkerDao {

    @Query("SELECT * FROM markers")
    fun getMarkers(): List<MarkerEntity>

    @Query("DELETE FROM markers WHERE latitude = :latitude AND longitude = :longitude")
    fun deleteByPoint(latitude: Double, longitude: Double)

    @Insert
    fun addMarker(marker: MarkerEntity): Long

    @Delete
    fun deleteMarker(marker: MarkerEntity)
}