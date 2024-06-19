import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.platform.LocalDensity
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
import dev.zwander.common.ui.rememberThemeInfo
import dev.zwander.common.util.BugsnagUtils.bugsnag
import dev.zwander.common.util.CrossPlatformBugsnag
import dev.zwander.common.util.LocalFrame
import dev.zwander.resources.common.MR
import io.github.mimoguz.customwindow.DwmAttribute
import io.github.mimoguz.customwindow.WindowHandle
import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs
import java.awt.Dimension
import java.util.UUID

private const val UUID_KEY = "bugsnag_user_id"

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

        CrossPlatformBugsnag.generateExtraErrorData().forEach { data ->
            it.addToTab(data.tabName, data.key, data.value)
        }
    }

    bugsnag.setAutoCaptureSessions(true)

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
            // For some reason this returns the title bar height on macOS.
            val menuBarHeight = remember {
                if (hostOs == OS.MacOS) window.height.dp else 0.dp
            }

            val density = LocalDensity.current

            LaunchedEffect(null) {
                // Set this after getting the original height.
                window.minimumSize = with(density) {
                    Dimension(200.dp.roundToPx(), 200.dp.roundToPx())
                }

                window.rootPane.putClientProperty("apple.awt.transparentTitleBar", true)
                window.rootPane.putClientProperty("apple.awt.fullWindowContent", true)
            }

            when (hostOs) {
                OS.Windows -> {
                    val themeInfo = rememberThemeInfo()

                    LaunchedEffect(themeInfo) {
                        try {
                            val handle = WindowHandle.tryFind(window)

                            handle.dwmSetBooleanValue(DwmAttribute.DWMWA_USE_IMMERSIVE_DARK_MODE, true)

                            themeInfo.colors.background.let {
                                handle.setCaptionColor(it)
                            }

                            themeInfo.colors.primary.let {
                                handle.setBorderColor(it)
                            }

                            themeInfo.colors.onBackground.let {
                                handle.setTextColor(it)
                            }
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
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

            CompositionLocalProvider(
                LocalFrame provides window,
            ) {
                App(
                    fullPadding = PaddingValues(
                        top = menuBarHeight,
                    ),
                )
            }
        }
    }
}
