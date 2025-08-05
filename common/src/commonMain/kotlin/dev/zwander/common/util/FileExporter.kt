package dev.zwander.common.util

import dev.zwander.kotlin.file.filekit.toKmpFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFileSaver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.Sink

object FileExporter {
    suspend fun saveFile(fileName: String, append: Boolean): Sink? {
        val dotIndex = fileName.lastIndexOf('.')
        val baseName = fileName.slice(0 until dotIndex)
        val extension = fileName.slice(dotIndex + 1 until fileName.length)

        val result = withContext(Dispatchers.Main) {
            FileKit.openFileSaver(suggestedName = baseName, extension = extension)
        }

        return result?.toKmpFile()?.openOutputStream(append)
    }
}
