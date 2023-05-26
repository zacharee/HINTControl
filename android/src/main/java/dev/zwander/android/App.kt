package dev.zwander.android

import android.app.Application
import com.bugsnag.android.Bugsnag

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        Bugsnag.start(this)
    }
}