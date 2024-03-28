package dev.zwander.common.util

import androidx.compose.runtime.Composable
import okio.BufferedSink

interface BufferedSinkCreator {
    suspend operator fun invoke(fileName: String, append: Boolean = false): BufferedSink?
}

expect object FileExporter {
    @Composable
    fun rememberBufferedSinkCreator(): BufferedSinkCreator
}