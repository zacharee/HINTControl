@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.data.rememberInfoList
import dev.zwander.common.data.set
import dev.zwander.common.model.MainModel
import dev.zwander.resources.common.MR
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun MainDataLayout(
    modifier: Modifier = Modifier
) {
    val data by MainModel.currentMainData.collectAsState()

    val items = rememberInfoList {
        this[MR.strings.apn] = data?.signal?.generic?.apn
        this[MR.strings.ipv6] = data?.signal?.generic?.hasIPv6?.toString()
        this[MR.strings.registration] = data?.signal?.generic?.registration
        this[MR.strings.roaming] = data?.signal?.generic?.roaming?.toString()
    }

    EmptiableContent(
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
