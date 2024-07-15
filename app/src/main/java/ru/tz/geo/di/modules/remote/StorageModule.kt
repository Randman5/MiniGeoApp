package ru.tz.geo.di.modules.remote

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.tz.geo.data.database.Database
import ru.tz.geo.data.database.repository.MarkerDao
import javax.inject.Singleton


@Module
class StorageModule {

    @Singleton
    @Provides
    fun getDatabase(context:Context): Database {
        return Room.databaseBuilder(context, Database::class.java, "database").build()
    }

    @Provides
    fun getMarkerDao(db:Database): MarkerDao = db.getMarkerDao()
}