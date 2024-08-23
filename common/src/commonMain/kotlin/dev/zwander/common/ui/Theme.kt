@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.ui

import androidx.compose.runtime.Composable
import dev.zwander.compose.DynamicMaterialTheme
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun Theme(
    content: @Composable () -> Unit,
) {
    DynamicMaterialTheme(content)
}
