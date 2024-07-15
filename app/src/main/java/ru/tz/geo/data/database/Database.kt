package ru.tz.geo.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.tz.geo.data.database.entity.MarkerEntity
import ru.tz.geo.data.database.repository.MarkerDao


@Database(entities = [
MarkerEntity::class
], version = 1)
abstract class Database: RoomDatabase() {
    abstract fun getMarkerDao(): MarkerDao
}