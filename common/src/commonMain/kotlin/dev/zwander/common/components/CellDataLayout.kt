@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.data.rememberInfoList
import dev.zwander.common.data.set
import dev.zwander.common.model.adapters.AdvancedDataLTE
import dev.zwander.common.model.adapters.BaseAdvancedData
import dev.zwander.common.model.adapters.BaseCellData
import dev.zwander.common.model.adapters.CellDataLTE
import dev.zwander.common.util.PersistentMutableStateFlow
import dev.zwander.common.util.bulletedList
import dev.zwander.resources.common.MR
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun CellBars(
    bars: Int?,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = bars,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(
                when (it) {
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
        )
    }
}

@Composable
@HiddenFromObjC
fun CellDataLayout(
    data: BaseCellData?,
    advancedData: BaseAdvancedData?,
    expandedKey: String,
    modifier: Modifier = Modifier,
) {
    val basicItems = rememberInfoList {
        this[MR.strings.bands] = data?.bands?.bulletedList()
        this[MR.strings.rsrp] = Triple(data?.rsrp, -115, -77)
        this[MR.strings.rsrq] = Triple(data?.rsrq, -25, -9)
        this[MR.strings.rssi] = Triple(data?.rssi, -95, -65)
        this[MR.strings.sinr] = Triple(data?.sinr, 2, 19)
        this[MR.strings.cid] = data?.cid?.toString()
        this[if (data is CellDataLTE?) MR.strings.enbid else MR.strings.gnbid] = data?.nbid?.toString()
    }

    val advancedItems = rememberInfoList {
        this[MR.strings.bandwidth] = advancedData?.bandwidth
        this[MR.strings.mcc] = advancedData?.mcc
        this[MR.strings.mnc] = advancedData?.mnc
        this[MR.strings.plmn] = advancedData?.plmn
        this[MR.strings.status] = advancedData?.status?.toString()
        this[MR.strings.cqi] = Triple(advancedData?.cqi, 0, 12)
        this[(if (advancedData is AdvancedDataLTE?) MR.strings.earfcn else MR.strings.nrarfcn)] = advancedData?.earfcn
        this[(if (advancedData is AdvancedDataLTE?) MR.strings.nrarfcn else MR.strings.earfcn)] = null as? String?
        this[MR.strings.ecgi] = advancedData?.ecgi
        this[MR.strings.pci] = advancedData?.pci
        this[MR.strings.tac] = advancedData?.tac
        this[MR.strings.supportedBands] = advancedData?.supportedBands?.bulletedList()
    }

    EmptiableContent(
        content = {
            val expandedState = remember(expandedKey) {
                PersistentMutableStateFlow(expandedKey, false)
            }

            var expanded by expandedState.collectAsMutableState()

            SelectionContainer {
                InfoRow(
                    items = basicItems,
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 4.dp),
                )
            }

            AnimatedVisibility(
                visible = expanded,
                modifier = Modifier.fillMaxWidth(),
            ) {
                SelectionContainer {
                    InfoRow(
                        items = advancedItems,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }

            ExpanderCard(
                expanded = expanded,
                onExpandChange = { expanded = it },
            )
        },
        emptyContent = {
            Text(
                text = stringResource(MR.strings.not_connected),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
            )
        },
        isEmpty = basicItems.isEmpty(),
        modifier = modifier,
    )
}
