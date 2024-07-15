package ru.tz.geo.di.modules.useCaseModules

import dagger.Module
import dagger.Provides
import ru.tz.geo.data.database.repository.MarkerDao
import ru.tz.geo.data.mappers.MarkersMapper
import ru.tz.geo.domain.usecase.mapMarkersUseCases.AddMarkerUseCase
import ru.tz.geo.domain.usecase.mapMarkersUseCases.DeleteMarkerUseCase
import ru.tz.geo.domain.usecase.mapMarkersUseCases.GetMarkersUseCase
import javax.inject.Singleton

@Module
class MapMarkersUseCaseModule {

    @Provides
    @Singleton
    fun getGetMarkersUseCase(dao: MarkerDao, mapper: MarkersMapper): GetMarkersUseCase {
        return GetMarkersUseCase(
            dao,
            mapper
        )
    }

    @Provides
    @Singleton
    fun getAddMarkerUseCase(dao: MarkerDao, mapper: MarkersMapper): AddMarkerUseCase {
        return AddMarkerUseCase(
            dao,
            mapper
        )
    }

    @Provides
    @Singleton
    fun getDeleteMarkerUseCase(dao: MarkerDao, mapper: MarkersMapper): DeleteMarkerUseCase {
        return DeleteMarkerUseCase(
            dao,
            mapper
        )
    }
}