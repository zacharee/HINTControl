package dev.zwander.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import okio.BufferedSink
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer
import platform.AppKit.NSModalResponseOK
import platform.AppKit.NSSavePanel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.experimental.ExperimentalObjCRefinement

actual object FileExporter {
    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    @Composable
    actual fun rememberBufferedSinkCreator(): BufferedSinkCreator {
        return remember {
            object : BufferedSinkCreator {
                override suspend fun invoke(fileName: String, append: Boolean): BufferedSink? {
                    val savePanel = NSSavePanel()

                    savePanel.nameFieldStringValue = fileName
                    val path = suspendCoroutine { continuation ->
                        savePanel.beginWithCompletionHandler {
                            continuation.resume(
                                if (it == NSModalResponseOK) {
                                    savePanel.URL
                                } else {
                                    null
                                }
                            )
                        }
                    }

                    return path?.absoluteString?.let {
                        val fileHandle = FileSystem.SYSTEM.openReadWrite(
                            file = it.toPath(),
                            mustCreate = true,
                            mustExist = false
                        )

                        fileHandle.sink().buffer()
                    }
                }
            }
        }
    }
}
