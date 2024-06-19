package dev.zwander.common

import android.annotation.SuppressLint
import android.app.Application
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.bugsnag.android.Bugsnag
import com.getkeepsafe.relinker.ReLinker
import dev.zwander.common.model.SettingsModel
import dev.zwander.common.util.CrossPlatformBugsnag
import dev.zwander.common.widget.WidgetUpdaterService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

class App : Application(), CoroutineScope by MainScope() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var instance: App
    }

    private val jobScheduler by lazy { getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler }

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

    fun cancelWidgetRefresh() {
        jobScheduler.cancel(WidgetUpdaterService.JOB_ID)
    }

    fun scheduleWidgetRefresh() {
        val builder = JobInfo.Builder(WidgetUpdaterService.JOB_ID, ComponentName(this, WidgetUpdaterService::class.java))
        builder.setPeriodic(SettingsModel.widgetRefresh.value * 1000)
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)

        jobScheduler.schedule(builder.build())
    }
}