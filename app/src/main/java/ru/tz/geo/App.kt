package ru.tz.geo

import android.app.Application
import ru.tz.geo.di.AppComponent
import ru.tz.geo.di.DaggerAppComponent


class App : Application() {
    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }

    companion object  {
        private lateinit var app: App
        fun get() = app
    }


    private fun initializeComponent(): AppComponent {
        return DaggerAppComponent.factory().create(this)
    }
}