package ru.tz.geo.di.modules

import dagger.Module
import ru.tz.geo.di.modules.remote.MappersModule
import ru.tz.geo.di.modules.remote.StorageModule
import ru.tz.geo.di.modules.remote.UseCasesModule


@Module(
    includes = [
        StorageModule::class,
        MappersModule::class,
        UseCasesModule::class
    ]
)
class AppModule