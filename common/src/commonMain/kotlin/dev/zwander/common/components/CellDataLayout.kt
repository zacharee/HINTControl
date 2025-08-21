@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.data.generateInfoList
import dev.zwander.common.data.set
import dev.zwander.common.model.adapters.AdvancedDataLTE
import dev.zwander.common.model.adapters.BaseAdvancedData
import dev.zwander.common.model.adapters.BaseCellData
import dev.zwander.common.model.adapters.CellDataLTE
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
    val basicItems = generateInfoList(data) {
        this[MR.strings.bands, MR.strings.bands_helper_text] = data?.bands?.bulletedList()
        this[MR.strings.rsrp, MR.strings.rsrp_helper_text] = Triple(data?.rsrp, -115, -77)
        this[MR.strings.rsrq, MR.strings.rsrq_helper_text] = Triple(data?.rsrq, -25, -9)
        this[MR.strings.rssi, MR.strings.rssi_helper_text] = Triple(data?.rssi, -95, -65)
        this[MR.strings.sinr, MR.strings.snr_helper_text] = Triple(data?.sinr, 2, 19)
        this[MR.strings.cid, MR.strings.cid_helper_text] = data?.cid?.toString()
        this[if (data is CellDataLTE?) MR.strings.enbid else MR.strings.gnbid, MR.strings.nbid_helper_text] = data?.nbid?.toString()
        this[MR.strings.antenna_used] = data?.antennaUsed
    }

    val advancedItems = generateInfoList(advancedData) {
        this[MR.strings.bandwidth, MR.strings.bandwidth_helper_text] = advancedData?.bandwidth
        this[MR.strings.mcc, MR.strings.mcc_helper_text] = advancedData?.mcc
        this[MR.strings.mnc, MR.strings.mnc_helper_text] = advancedData?.mnc
        this[MR.strings.plmn, MR.strings.plmn_helper_text] = advancedData?.plmn
        this[MR.strings.status, MR.strings.status_helper_text] = advancedData?.status?.toString()
        this[MR.strings.cqi, MR.strings.cqi_index_helper_text] = Triple(advancedData?.cqi, 0, 12)
        this[(if (advancedData is AdvancedDataLTE?) MR.strings.earfcn else MR.strings.nrarfcn), MR.strings.arfcn_helper_text] = advancedData?.earfcn
        this[(if (advancedData is AdvancedDataLTE?) MR.strings.nrarfcn else MR.strings.earfcn), MR.strings.arfcn_helper_text] = null as? String?
        this[MR.strings.ecgi, MR.strings.cgi_helper_text] = advancedData?.ecgi
        this[MR.strings.pci, MR.strings.pci_helper_text] = advancedData?.pci
        this[MR.strings.tac, MR.strings.tac_helper_text] = advancedData?.tac
        this[MR.strings.supportedBands, MR.strings.supported_bands_helper_text] = advancedData?.supportedBands?.takeIf { it.isNotEmpty() }?.bulletedList()
    }

    EmptiableContent(
        content = {
            SelectionContainer {
                InfoRow(
                    items = basicItems,
                    modifier = Modifier.fillMaxWidth(),
                    advancedItems = advancedItems,
                    expandedKey = expandedKey,
                )
            }
        },
        emptyContent = {
            Text(
                text = stringResource(MR.strings.not_connected),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Normal,
            )
        },
        isEmpty = basicItems.values.filterNotNull().isEmpty(),
        modifier = modifier,
    )
}
