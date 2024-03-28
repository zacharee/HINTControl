package dev.zwander.common.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import korlibs.memory.Platform
import okio.BufferedSink
import okio.buffer
import okio.sink
import java.awt.FileDialog
import java.io.File
import javax.swing.JFileChooser

actual object FileExporter {
    @Composable
    actual fun rememberBufferedSinkCreator(): BufferedSinkCreator {
        val frame = LocalFrame.current

        return remember {
            object : BufferedSinkCreator {
                override suspend fun invoke(fileName: String, append: Boolean): BufferedSink? {
                    return if (Platform.isWindows) {
                        val chooser = JFileChooser().apply {
                            dialogType = JFileChooser.SAVE_DIALOG
                            selectedFile = File(fileName)
                        }

                        if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                            chooser.selectedFile.sink(append).buffer()
                        } else {
                            null
                        }
                    } else {
                        val dialog = FileDialog(frame).apply {
                            mode = FileDialog.SAVE
                            file = fileName
                            isVisible = true
                        }

                        dialog.files.firstOrNull()?.sink(append)?.buffer()
                    }
                }
            }
        }
    }
}