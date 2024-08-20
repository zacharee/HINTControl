package dev.zwander.common.util

import io.github.vinceglb.filekit.core.FileKit
import io.github.vinceglb.filekit.core.PlatformFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.BufferedSink

object FileExporter {
    suspend fun saveFile(fileName: String, append: Boolean): BufferedSink? {
        val dotIndex = fileName.lastIndexOf('.')
        val baseName = fileName.slice(0 until dotIndex)
        val extension = fileName.slice(dotIndex + 1 until fileName.length)

        val result = withContext(Dispatchers.Main) {
            FileKit.saveFile(baseName = baseName, extension = extension)
        }

        return result?.bufferedSink(append)
    }
}

expect fun PlatformFile.bufferedSink(append: Boolean = false): BufferedSink?
