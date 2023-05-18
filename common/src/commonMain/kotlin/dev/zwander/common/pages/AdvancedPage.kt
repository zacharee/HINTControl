package dev.zwander.common.pages

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.InfoRow
import dev.zwander.common.components.PageGrid
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.adapters.BaseAdvancedData
import dev.zwander.resources.common.MR

private data class AdvancedItemData(
    val title: StringResource,
    val blocks: List<Pair<StringResource, Any?>>
)

@Composable
fun AdvancedPage(
    modifier: Modifier = Modifier,
) {
    val cellData by MainModel.currentCellData.collectAsState()
    val simData by MainModel.currentSimData.collectAsState()

    val items = remember(cellData, simData) {
        listOf(
            AdvancedItemData(
                title = MR.strings.lte,
                blocks = generateBaseCellItems(cellData?.cell?.fourG)
            ),
            AdvancedItemData(
                title = MR.strings.five_g,
                blocks = generateBaseCellItems(cellData?.cell?.fiveG)
            ),
            AdvancedItemData(
                title = MR.strings.sim,
                blocks = listOf(
                    MR.strings.iccid to simData?.sim?.iccId,
                    MR.strings.imei to simData?.sim?.imei,
                    MR.strings.imsi to simData?.sim?.imsi,
                    MR.strings.msisdn to simData?.sim?.msisdn,
                    MR.strings.status to simData?.sim?.status,
                )
            )
        )
    }

    PageGrid(
        items = items,
        modifier = modifier,
        renderItemTitle = {
            Text(
                text = stringResource(it.title),
            )
        },
        renderItem = {
            if (it.blocks.isNotEmpty()) {
                InfoRow(
                    items = it.blocks,
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                Text(
                    text = stringResource(MR.strings.not_connected),
                )
            }
        },
    )
}

private fun generateBaseCellItems(data: BaseAdvancedData?): List<Pair<StringResource, Any?>> {
    val allItems = listOf(
        MR.strings.bandwidth to data?.bandwidth,
        MR.strings.mcc to data?.mcc,
        MR.strings.mnc to data?.mnc,
        MR.strings.plmn to data?.plmn,
        MR.strings.status to data?.status,
        MR.strings.cqi to data?.cqi,
        MR.strings.earfcn to data?.earfcn,
        MR.strings.ecgi to data?.ecgi,
        MR.strings.pci to data?.pci,
        MR.strings.tac to data?.tac,
        MR.strings.supportedBands to data?.supportedBands?.joinToString(" • ")
    )

    return allItems.filter { it.second != null }
}
