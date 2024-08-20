package dev.zwander.common.util

import dev.zwander.common.App
import io.github.vinceglb.filekit.core.PlatformFile
import okio.BufferedSink
import okio.buffer
import okio.sink

actual fun PlatformFile.bufferedSink(append: Boolean): BufferedSink? {
    return App.instance.contentResolver.openOutputStream(
        uri,
        if (append) "wa" else "w",
    )?.sink()?.buffer()
}
