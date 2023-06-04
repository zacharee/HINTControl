package dev.zwander.common.components.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.zwander.common.components.InfoRow
import dev.zwander.resources.common.MR

@Composable
fun Credits() {
    InfoRow(
        items = remember {
            listOf(
                MR.strings.creator to MR.strings.zachary_wander,
                MR.strings.nokia_tester to MR.strings.shad,
            )
        },
        modifier = Modifier.fillMaxWidth(),
    )
}
