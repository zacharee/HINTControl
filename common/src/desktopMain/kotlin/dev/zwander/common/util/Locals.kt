package dev.zwander.common.util

import androidx.compose.runtime.compositionLocalOf
import javax.swing.JFrame

val LocalFrame = compositionLocalOf<JFrame> { throw IllegalStateException("Frame not specified!") }
