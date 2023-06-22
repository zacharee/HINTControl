import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.russhwolf.settings.Settings
import dev.icerock.moko.resources.compose.painterResource
import dev.zwander.common.App
import dev.zwander.common.GradleConfig
import dev.zwander.common.ui.getThemeInfo
import dev.zwander.common.util.BugsnagUtils.bugsnag
import dev.zwander.resources.common.MR
import io.github.mimoguz.custom_window.DwmAttribute
import io.github.mimoguz.custom_window.StageOps
import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs
import java.util.UUID

private const val UUID_KEY = "bugsnag_user_id"

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    System.setProperty("apple.laf.useScreenMenuBar", "true")
    System.setProperty("apple.awt.application.appearance", "system")
    System.setProperty("apple.awt.application.name", MR.strings.app_name.localized())

    val settings = Settings()

    val uuid = settings.getStringOrNull(UUID_KEY) ?: UUID.randomUUID().toString().also {
        settings.putString(UUID_KEY, it)
    }

    bugsnag.setAppVersion(GradleConfig.versionName)
    bugsnag.addCallback {
        it.setUserId(uuid)
        it.addToTab("app", "version_code", GradleConfig.versionCode)
        it.addToTab("app", "jdk_architecture", System.getProperty("sun.arch.data.model"))
    }

    when (hostOs) {
        OS.Windows -> {
            System.setProperty("skiko.renderApi", "OPENGL")
        }
        else -> {
            /* no-op */
        }
    }

    application {
        val windowState = rememberWindowState()

        Window(
            onCloseRequest = ::exitApplication,
            title = MR.strings.app_name.localized(),
            state = windowState,
            icon = painterResource(MR.images.icon),
        ) {
            window.rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
            window.rootPane.putClientProperty("apple.awt.fullWindowContent", true)

            // For some reason this returns the title bar height on macOS.
            val menuBarHeight = if (hostOs == OS.MacOS) window.height.dp else 0.dp

            when (hostOs) {
                OS.Windows -> {
                    val handle = StageOps.findWindowHandle(window)
                    StageOps.dwmSetBooleanValue(
                        handle,
                        DwmAttribute.DWMWA_USE_IMMERSIVE_DARK_MODE,
                        false,
                    )
                    getThemeInfo().colors?.background?.let {
                        StageOps.setCaptionColor(
                            handle,
                            it
                        )
                    }
                    getThemeInfo().colors?.onBackground?.let {
                        StageOps.setTextColor(
                            handle,
                            it
                        )
                    }
                }
                OS.MacOS -> {
                    MenuBar {
                        Menu(
                            text = MR.strings.window.localized(),
                        ) {
                            Item(
                                text = MR.strings.minimize.localized(),
                                onClick = {
                                    windowState.isMinimized = true
                                },
                                shortcut = KeyShortcut(Key.M, meta = true)
                            )

                            Item(
                                text = MR.strings.zoom.localized(),
                                onClick = {
                                    windowState.placement = WindowPlacement.Maximized
                                }
                            )

                            Item(
                                text = MR.strings.close.localized(),
                                onClick = {
                                    exitApplication()
                                },
                                shortcut = KeyShortcut(Key.W, meta = true)
                            )
                        }
                    }
                }
                else -> {
                    /* no-op */
                }
            }

            App(
                fullPadding = PaddingValues(
                    top = menuBarHeight,
                ),
            )
        }
    }
}
