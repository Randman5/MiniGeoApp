package ru.tz.geo.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.tz.geo.di.modules.AppModule
import ru.tz.geo.ui.fragments.map.MapFragmentViewModel
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): AppComponent
    }

    fun inject(viewModel: MapFragmentViewModel)

}