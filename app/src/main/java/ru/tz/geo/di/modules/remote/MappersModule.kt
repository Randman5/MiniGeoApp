package ru.tz.geo.di.modules.remote

import dagger.Module
import dagger.Provides
import ru.tz.geo.data.mappers.MarkersMapper
import javax.inject.Singleton

@Module
class MappersModule {

    @Provides
    @Singleton
    fun getMarkerMapper(): MarkersMapper = MarkersMapper()
}
