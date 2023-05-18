import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.window.*
import dev.zwander.common.App
import dev.zwander.common.ui.getThemeInfo
import dev.zwander.resources.common.MR
import io.github.mimoguz.custom_window.DwmAttribute
import io.github.mimoguz.custom_window.StageOps
import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs


@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    System.setProperty("apple.laf.useScreenMenuBar", "true")
    System.setProperty("apple.awt.application.appearance", "system")
    System.setProperty("apple.awt.application.name", MR.strings.app_name.localized())

    if (hostOs == OS.Windows) {
        System.setProperty("skiko.renderApi", "OPENGL")
    }

    application {
        val windowState = rememberWindowState()

        Window(
            onCloseRequest = ::exitApplication,
            title = MR.strings.app_name.localized(),
            state = windowState,
        ) {
            if (hostOs == OS.Windows) {
                val handle = StageOps.findWindowHandle(this.window)
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

            if (hostOs == OS.MacOS) {
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

//                Menu(
//                    text = strings.help()
//                ) {
//                    Item(
//                        text = strings.github(),
//                        onClick = {
//                            UrlHandler.launchUrl("https://github.com/zacharee/SamloaderKotlin")
//                        }
//                    )
//
//                    Item(
//                        text = strings.mastodon(),
//                        onClick = {
//                            UrlHandler.launchUrl("https://androiddev.social/@wander1236")
//                        }
//                    )
//
//                    Item(
//                        text = strings.twitter(),
//                        onClick = {
//                            UrlHandler.launchUrl("https://twitter.com/wander1236")
//                        }
//                    )
//
//                    Item(
//                        text = strings.patreon(),
//                        onClick = {
//                            UrlHandler.launchUrl("https://patreon.com/zacharywander")
//                        }
//                    )
//
//                    Item(
//                        text = strings.supporters(),
//                        onClick = {
//                            showingSupportersWindow = true
//                        }
//                    )
//                }
                }
            }

            App()
        }
    }
}
