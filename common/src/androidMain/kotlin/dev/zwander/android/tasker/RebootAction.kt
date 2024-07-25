package dev.zwander.android.tasker

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.joaomgcd.taskerpluginlibrary.action.TaskerPluginRunnerActionNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigHelperNoOutputOrInput
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfigNoInput
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResult
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultError
import com.joaomgcd.taskerpluginlibrary.runner.TaskerPluginResultSucess
import dev.zwander.common.model.GlobalModel
import kotlinx.coroutines.runBlocking

class RebootActionConfig : AppCompatActivity(), TaskerPluginConfigNoInput {
    override val context: Context
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val helper = RebootActionHelper(this)
        helper.onCreate()
        helper.finishForTasker()
    }
}

class RebootActionHelper(config: TaskerPluginConfigNoInput) : TaskerPluginConfigHelperNoOutputOrInput<RebootActionRunner>(config) {
    override val runnerClass: Class<RebootActionRunner> = RebootActionRunner::class.java
}

class RebootActionRunner : TaskerPluginRunnerActionNoOutputOrInput() {
    override fun run(context: Context, input: TaskerInput<Unit>): TaskerPluginResult<Unit> {
        return runBlocking {
            GlobalModel.updateClient()
            GlobalModel.httpClient.value?.reboot()

            GlobalModel.httpError.value?.let {
                TaskerPluginResultError(it)
            } ?: TaskerPluginResultSucess()
        }
    }
}
