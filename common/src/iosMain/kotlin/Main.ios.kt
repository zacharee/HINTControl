@file:Suppress("FunctionName", "unused")

import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.rickclephas.kmp.nsexceptionkt.bugsnag.cinterop.BugsnagConfiguration
import com.rickclephas.kmp.nsexceptionkt.bugsnag.configureBugsnag
import com.rickclephas.kmp.nsexceptionkt.bugsnag.setBugsnagUnhandledExceptionHook
import dev.zwander.common.App
import dev.zwander.common.util.Bugsnag

fun MainViewController() = ComposeUIViewController {
    App(
        modifier = Modifier,
    )
}

fun updateBugsnagConfig(config: BugsnagConfiguration) {
    configureBugsnag(config)

    cocoapods.Bugsnag.BugsnagConfiguration.loadConfig().apply {
        addOnSendErrorBlock {
            Bugsnag.generateExtraErrorData().forEach { data ->
                it?.addMetadata(data.tabName, data.key, data.value.toString())
            }
            true
        }
    }
}

fun setupBugsnag() {
    setBugsnagUnhandledExceptionHook()
}
