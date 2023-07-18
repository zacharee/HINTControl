package dev.zwander.common.widget

import android.app.job.JobParameters
import android.app.job.JobService
import androidx.glance.appwidget.updateAll
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class WidgetUpdaterService : JobService(), CoroutineScope by MainScope() {
    companion object {
        const val JOB_ID = 10001
    }

    private val job = atomic<Job?>(null)

    override fun onStartJob(params: JobParameters?): Boolean {
        job.value = launch {
            ConnectionStatusWidget().updateAll(this@WidgetUpdaterService)
        }
        return false
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        job.getAndSet(null)?.cancel()
        return true
    }
}