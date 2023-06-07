import androidx.compose.ui.window.Window
import dev.icerock.moko.resources.desc.desc
import dev.zwander.common.App
import dev.zwander.resources.common.MR
import platform.AppKit.NSApplication
import platform.AppKit.NSApplicationActivationPolicy
import platform.AppKit.NSApplicationDelegateProtocol
import platform.darwin.NSObject

@Suppress("UNUSED_PARAMETER")
fun main(args: Array<String>) {
    val app = NSApplication.sharedApplication()
    app.setActivationPolicy(NSApplicationActivationPolicy.NSApplicationActivationPolicyRegular)

    app.delegate = object : NSObject(), NSApplicationDelegateProtocol {
        override fun applicationShouldTerminateAfterLastWindowClosed(sender: NSApplication): Boolean {
            return true
        }
    }

    Window(MR.strings.app_name.desc().localized()) {
        App()
    }

    app.run()
}
