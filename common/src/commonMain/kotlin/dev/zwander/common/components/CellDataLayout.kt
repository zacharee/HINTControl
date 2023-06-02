@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.model.adapters.BaseCellData
import dev.zwander.common.model.adapters.CellData5G
import dev.zwander.common.model.adapters.CellDataLTE
import dev.zwander.resources.common.MR
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun CellBars(
    bars: Int?,
    modifier: Modifier = Modifier,
) {
    Icon(
        painter = painterResource(
            when (bars) {
                0 -> MR.images.cell_0
                1 -> MR.images.cell_1
                2 -> MR.images.cell_2
                3 -> MR.images.cell_3
                4 -> MR.images.cell_4
                5 -> MR.images.cell_5
                else -> MR.images.cell_none
            }
        ),
        contentDescription = stringResource(MR.strings.strength),
        modifier = modifier,
    )
}

@Composable
@HiddenFromObjC
fun CellDataLayout(
    data: BaseCellData?,
    modifier: Modifier = Modifier,
) {
    val items = remember(data) {
        generateBasicCellItems(data)
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
                text = stringResource(MR.strings.not_connected),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
            )
        },
        isEmpty = items.isEmpty(),
        modifier = modifier,
    )
}

fun generateBasicCellItems(data: BaseCellData?): List<Pair<StringResource, Any?>> {
    val allItems = listOf(
        MR.strings.bands to data?.bands?.joinToString(" • "),
        MR.strings.rsrp to data?.rsrp,
        MR.strings.rsrq to data?.rsrq,
        MR.strings.rssi to data?.rssi,
        MR.strings.sinr to data?.sinr,
        MR.strings.cid to data?.cid,
    ) + when (data) {
        is CellDataLTE -> listOf(
            MR.strings.enbid to data.eNBID
        )

        is CellData5G -> listOf(
            MR.strings.gnbid to data.gNBID
        )

        else -> listOf()
    }

    return allItems.filter { it.second != null }
}
