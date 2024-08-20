package dev.zwander.common.util

import io.github.vinceglb.filekit.core.PlatformFile
import okio.BufferedSink
import okio.buffer
import okio.sink

actual fun PlatformFile.bufferedSink(append: Boolean): BufferedSink? {
    return file.sink(append).buffer()
}
