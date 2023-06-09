@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.model.MainModel
import dev.zwander.common.util.addAll
import dev.zwander.common.util.buildItemList
import dev.zwander.resources.common.MR
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun MainDataLayout(
    modifier: Modifier = Modifier
) {
    val data by MainModel.currentMainData.collectAsState()

    val items = remember(data) {
        buildItemList {
            addAll(
                MR.strings.apn to data?.signal?.generic?.apn,
                MR.strings.ipv6 to data?.signal?.generic?.hasIPv6,
                MR.strings.registration to data?.signal?.generic?.registration,
                MR.strings.roaming to data?.signal?.generic?.roaming,
            )
        }
    }

    EmptyableContent(
        content = {
            InfoRow(
                items = items,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        emptyContent = {
            Text(
                text = stringResource(MR.strings.unavailable),
            )
        },
        isEmpty = items.isEmpty(),
        modifier = modifier,
    )
}
