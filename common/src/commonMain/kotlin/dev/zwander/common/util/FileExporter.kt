package dev.zwander.common.util

import androidx.compose.runtime.Composable
import okio.BufferedSink
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

interface BufferedSinkCreator {
    suspend operator fun invoke(fileName: String, append: Boolean = false): BufferedSink?
}

expect object FileExporter {
    @OptIn(ExperimentalObjCRefinement::class)
    @HiddenFromObjC
    @Composable
    fun rememberBufferedSinkCreator(): BufferedSinkCreator
}