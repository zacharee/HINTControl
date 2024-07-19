package dev.zwander.common.components.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.zwander.common.components.InfoRow
import dev.zwander.common.data.rememberInfoList
import dev.zwander.common.data.set
import dev.zwander.resources.common.MR

@Composable
fun Credits() {
    InfoRow(
        items = rememberInfoList {
            this[MR.strings.creator] = MR.strings.zachary_wander
            this[MR.strings.nokia_tester] = MR.strings.shad
        },
        modifier = Modifier.fillMaxWidth(),
    )
}
