package dev.zwander.common.util

import io.github.vinceglb.filekit.core.PlatformFile
import okio.BufferedSink
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.buffer

actual fun PlatformFile.bufferedSink(append: Boolean): BufferedSink? {
    return nsUrl.absoluteURL?.path?.toPath()?.let { path ->
        FileSystem.SYSTEM.run {
            if (append) {
                appendingSink(path, mustExist = false)
            } else {
                sink(path, mustCreate = false)
            }.buffer()
        }
    }
}
