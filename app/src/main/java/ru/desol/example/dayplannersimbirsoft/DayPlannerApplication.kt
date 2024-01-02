package ru.desol.example.dayplannersimbirsoft

import android.app.Application
import timber.log.Timber

class DayPlannerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}