@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE")

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.russhwolf.settings.Settings
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.compose.painterResource
import dev.zwander.common.App
import dev.zwander.common.GradleConfig
import dev.zwander.common.data.Page
import dev.zwander.common.model.GlobalModel
import dev.zwander.common.util.BugsnagUtils
import dev.zwander.common.util.BugsnagUtils.bugsnag
import dev.zwander.common.util.CrossPlatformBugsnag
import dev.zwander.common.util.LocalFrame
import dev.zwander.common.util.jna.Kernel32
import dev.zwander.compose.alertdialog.LocalWindowDecorations
import dev.zwander.compose.rememberThemeInfo
import dev.zwander.resources.common.MR
import io.github.mimoguz.customwindow.DwmAttribute
import io.github.mimoguz.customwindow.WindowHandle
import korlibs.platform.Platform
import org.jetbrains.skia.DirectContext
import org.jetbrains.skiko.GraphicsApi
import org.jetbrains.skiko.OS
import org.jetbrains.skiko.RenderException
import org.jetbrains.skiko.SkiaLayer
import org.jetbrains.skiko.hostOs
import oshi.SystemInfo
import java.awt.Desktop
import java.awt.Dimension
import java.awt.EventQueue
import java.util.UUID

private const val UUID_KEY = "bugsnag_user_id"

fun main() {
    System.setProperty("apple.laf.useScreenMenuBar", "true")
    System.setProperty("apple.awt.application.appearance", "system")
    System.setProperty("apple.awt.application.name", GradleConfig.appName)

    val settings = Settings()

    val uuid = settings.getStringOrNull(UUID_KEY) ?: UUID.randomUUID().toString().also {
        settings.putString(UUID_KEY, it)
    }
    val oshiSystemInfo = SystemInfo()

    bugsnag.setAppVersion(GradleConfig.versionName)
    bugsnag.addCallback {
        it.setUserId(uuid)
        it.addToTab("device", "manufacturer", oshiSystemInfo.hardware.computerSystem.manufacturer)
        it.addToTab("device", "model", oshiSystemInfo.hardware.computerSystem.model)
        it.addToTab("device", "memory", oshiSystemInfo.hardware.memory.total)
        it.addToTab("device", "motherboard", oshiSystemInfo.hardware.computerSystem.baseboard.model)
        it.addToTab("device", "firmwareVersion", oshiSystemInfo.hardware.computerSystem.firmware.version)
        it.addToTab("device", "processorModel", oshiSystemInfo.hardware.processor.processorIdentifier.model)
        it.addToTab("device", "processorFamily", oshiSystemInfo.hardware.processor.processorIdentifier.family)
        it.addToTab("device", "processorName", oshiSystemInfo.hardware.processor.processorIdentifier.name)
        it.addToTab("app", "version_code", GradleConfig.versionCode)
        it.addToTab("app", "jdk_architecture", System.getProperty("sun.arch.data.model"))

        CrossPlatformBugsnag.generateExtraErrorData().forEach { data ->
            it.addToTab(data.tabName, data.key, data.value)
        }
    }

    bugsnag.setAutoCaptureSessions(true)

    when {
        Platform.isLinux -> {
            val context = try {
                DirectContext.makeGL()
            } catch (e: Throwable) {
                null
            }

            try {
                context?.flush()
            } catch (e: Throwable) {
                BugsnagUtils.notify(
                    IllegalStateException(
                        "Unable to flush OpenGL context, using software rendering.",
                        e
                    )
                )
                System.setProperty("skiko.renderApi", "SOFTWARE_FAST")
            } finally {
                try {
                    context?.close()
                } catch (_: Throwable) {
                }
            }
        }

        Platform.isWindows -> {
            if (Kernel32.isEmulatedX86()) {
                EventQueue.invokeAndWait {
                    val layer = SkiaLayer()
                    try {
                        layer.inDrawScope {
                            throw RenderException()
                        }
                    } catch (_: Throwable) {}

                    if (layer.renderApi == GraphicsApi.OPENGL) {
                        BugsnagUtils.notify(IllegalStateException("Skiko chose OpenGL on ARM, falling back to software rendering."))
                        System.setProperty("skiko.renderApi", "SOFTWARE_FAST")
                    }

                    layer.dispose()
                }
            }
        }
    }

    application {
        val windowState = rememberWindowState(
            size = DpSize(900.dp, 600.dp),
        )
        var currentPage by GlobalModel.currentPage.collectAsMutableState()

        DisposableEffect(null) {
            if (Desktop.getDesktop().isSupported(Desktop.Action.APP_ABOUT)) {
                Desktop.getDesktop().setAboutHandler {
                    currentPage = Page.SettingsPage
                }
            }

            onDispose {
                if (Desktop.getDesktop().isSupported(Desktop.Action.APP_ABOUT)) {
                    Desktop.getDesktop().setAboutHandler(null)
                }
            }
        }

        Window(
            onCloseRequest = ::exitApplication,
            title = GradleConfig.appName,
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
                                shortcut = KeyShortcut(Key.M, meta = true),
                            )

                            Item(
                                text = MR.strings.zoom.localized(),
                                onClick = {
                                    windowState.placement = WindowPlacement.Maximized
                                },
                            )

                            Item(
                                text = MR.strings.close.localized(),
                                onClick = {
                                    exitApplication()
                                },
                                shortcut = KeyShortcut(Key.W, meta = true),
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
                LocalWindowDecorations provides DpRect(0.dp, menuBarHeight, 0.dp, 0.dp),
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
