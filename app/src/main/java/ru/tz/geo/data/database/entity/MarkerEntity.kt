package ru.tz.geo.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "markers",
    indices = [
        Index(value = ["latitude", "longitude"], unique = true)
    ]
)
data class MarkerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val latitude: Double,
    val longitude: Double,
    val text: String,
)