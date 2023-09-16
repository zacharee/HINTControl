@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.pages

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.CellBars
import dev.zwander.common.components.EmptiableContent
import dev.zwander.common.components.InfoRow
import dev.zwander.common.components.PageGrid
import dev.zwander.common.components.generateBasicCellItems
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.adapters.AdvancedDataLTE
import dev.zwander.common.model.adapters.BaseAdvancedData
import dev.zwander.common.model.adapters.BaseCellData
import dev.zwander.common.util.addAll
import dev.zwander.common.util.buildItemList
import dev.zwander.common.util.bulletedList
import dev.zwander.resources.common.MR
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

private data class AdvancedItemData(
    val title: StringResource,
    val blocks: List<Pair<StringResource, Any?>>,
    val emptyMessage: StringResource,
    val titleAccessory: (@Composable () -> Unit)? = null,
)

@Composable
@HiddenFromObjC
fun AdvancedPage(
    modifier: Modifier = Modifier,
) {
    val cellData by MainModel.currentCellData.collectAsState()
    val basicData by MainModel.currentMainData.collectAsState()
    val simData by MainModel.currentSimData.collectAsState()

    val items = remember(cellData, simData, basicData) {
        listOf(
            AdvancedItemData(
                title = MR.strings.lte,
                blocks = generateBaseCellItems(
                    cellData?.cell?.fourG,
                    basicData?.signal?.fourG,
                ),
                emptyMessage = MR.strings.not_connected,
                titleAccessory = {
                    CellBars(
                        bars = basicData?.signal?.fourG?.bars?.toInt(),
                    )
                },
            ),
            AdvancedItemData(
                title = MR.strings.five_g,
                blocks = generateBaseCellItems(
                    cellData?.cell?.fiveG,
                    basicData?.signal?.fiveG,
                ),
                emptyMessage = MR.strings.not_connected,
                titleAccessory = {
                    CellBars(
                        bars = basicData?.signal?.fiveG?.bars?.toInt(),
                    )
                },
            ),
            AdvancedItemData(
                title = MR.strings.sim,
                blocks = buildItemList {
                    addAll(
                        MR.strings.iccid to simData?.sim?.iccId,
                        MR.strings.imei to simData?.sim?.imei,
                        MR.strings.imsi to simData?.sim?.imsi,
                        MR.strings.msisdn to simData?.sim?.msisdn,
                        MR.strings.status to simData?.sim?.status,
                    )
                },
                emptyMessage = MR.strings.unavailable,
            )
        )
    }

    PageGrid(
        items = items,
        modifier = modifier,
        renderItemTitle = {
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(it.title),
                )

                Spacer(modifier = Modifier.weight(1f))

                it.titleAccessory?.let { accessory ->
                    accessory()
                }
            }
        },
        renderItem = {
            EmptiableContent(
                content = {
                    InfoRow(
                        items = it.blocks,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                emptyContent = {
                    Text(
                        text = stringResource(it.emptyMessage),
                    )
                },
                isEmpty = it.blocks.isEmpty(),
            )
        },
    )
}

private fun generateBaseCellItems(data: BaseAdvancedData?, basicData: BaseCellData?): List<Pair<StringResource, Any?>> {
    val basicItems = generateBasicCellItems(basicData)

    val allItems = buildItemList {
        addAll(
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
            MR.strings.supportedBands to data?.supportedBands?.bulletedList(),
        )
    }

    return basicItems + allItems
}
