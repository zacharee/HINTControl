@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumTouchTargetEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.model.adapters.AdvancedDataLTE
import dev.zwander.common.model.adapters.BaseAdvancedData
import dev.zwander.common.model.adapters.BaseCellData
import dev.zwander.common.model.adapters.CellData5G
import dev.zwander.common.model.adapters.CellDataLTE
import dev.zwander.common.util.PersistentMutableStateFlow
import dev.zwander.common.util.filterBlanks
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@HiddenFromObjC
fun CellDataLayout(
    data: BaseCellData?,
    advancedData: BaseAdvancedData?,
    expandedKey: String,
    modifier: Modifier = Modifier,
) {
    val basicItems = remember(data) {
        generateBasicCellItems(data)
    }

    val advancedItems = remember(advancedData) {
        generateAdvancedCellItems(advancedData)
    }

    EmptyableContent(
        content = {
            val expandedState = remember(expandedKey) {
                PersistentMutableStateFlow(expandedKey, false)
            }

            var expanded by expandedState.collectAsMutableState()

            InfoRow(
                items = basicItems,
                modifier = Modifier.fillMaxWidth(),
            )

            AnimatedVisibility(
                visible = expanded,
                modifier = Modifier.fillMaxWidth(),
            ) {
                InfoRow(
                    items = advancedItems,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            CompositionLocalProvider(
                LocalMinimumTouchTargetEnforcement provides false,
            ) {
                Card(
                    onClick = {
                        expanded = !expanded
                    },
                    colors = CardDefaults.outlinedCardColors(),
                    elevation = CardDefaults.outlinedCardElevation(),
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center,
                    ) {
                        val rotation by animateFloatAsState(if (expanded) 180f else 0f)

                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = stringResource(if (expanded) MR.strings.collapse else MR.strings.expand),
                            modifier = Modifier.rotate(rotation),
                        )
                    }
                }
            }
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

private fun generateAdvancedCellItems(
    data: BaseAdvancedData?,
): List<Pair<StringResource, Any?>> {
    val allItems = listOf(
        MR.strings.bandwidth to data?.bandwidth,
        MR.strings.mcc to data?.mcc,
        MR.strings.mnc to data?.mnc,
        MR.strings.plmn to data?.plmn,
        MR.strings.status to data?.status,
        MR.strings.cqi to data?.cqi,
        (if (data is AdvancedDataLTE?) MR.strings.earfcn else MR.strings.nrarfcn) to data?.earfcn,
        MR.strings.ecgi to data?.ecgi,
        MR.strings.pci to data?.pci,
        MR.strings.tac to data?.tac,
        MR.strings.supportedBands to data?.supportedBands?.joinToString(" • ")
    )

    return allItems.filterBlanks()
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

    return allItems.filterBlanks()
}
