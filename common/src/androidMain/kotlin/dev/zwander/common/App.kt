package dev.zwander.common

import android.annotation.SuppressLint
import android.app.Application
import com.bugsnag.android.Bugsnag
import com.getkeepsafe.relinker.ReLinker
import dev.zwander.common.util.CrossPlatformBugsnag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class App : Application(), CoroutineScope by MainScope() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var instance: App? = null
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        ReLinker.loadLibrary(this, "bugsnag-plugin-android-anr")
        Bugsnag.start(this).apply {
            this.addOnError {
                CrossPlatformBugsnag.generateExtraErrorData().forEach { data ->
                    it.addMetadata(data.tabName, data.key, data.value)
                }
                true
            }
        }
    }
}