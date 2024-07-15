package ru.tz.geo.di.modules.remote

import dagger.Module
import ru.tz.geo.di.modules.useCaseModules.MapMarkersUseCaseModule

@Module(includes = [
    MapMarkersUseCaseModule::class
])
class UseCasesModule