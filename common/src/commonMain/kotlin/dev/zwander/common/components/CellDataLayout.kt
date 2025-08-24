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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.adapters.AdvancedData5G
import dev.zwander.common.model.adapters.AdvancedDataLTE
import dev.zwander.common.model.adapters.BaseAdvancedData
import dev.zwander.common.model.adapters.BaseCellData
import dev.zwander.common.model.adapters.CellData5G
import dev.zwander.common.model.adapters.CellDataLTE
import dev.zwander.common.util.bulletedList
import dev.zwander.common.util.deriveCidGnbidFromEcgi
import dev.zwander.common.util.SQSICalculator
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

/**
 * Renders current cell information (LTE or 5G) with basic and advanced fields.
 *
 * Special handling for 5G gNBID/CID derivation:
 * - If the current APN equals "FBB.HOME" (case-insensitive) and the reported 5G gNBID is negative,
 *   derive gNBID and CID from ECGI by removing PLMN and splitting the 36-bit remainder
 *   using T‑Mobile's convention (gNBID 24 bits / CID 12 bits). The derived values are shown
 *   in place of the reported ones. LTE values are unaffected.
 */
@Composable
@HiddenFromObjC
fun CellDataLayout(
    data: BaseCellData?,
    advancedData: BaseAdvancedData?,
    expandedKey: String,
    modifier: Modifier = Modifier,
) {
    val mainData by MainModel.currentMainData.collectAsState()

    // T-Mobile Home APN
    val isFbbHomeApn =
        mainData?.signal?.generic?.apn?.equals("FBB.HOME", ignoreCase = true) == true

    val shouldDerive = isFbbHomeApn &&
            (data is CellData5G?) &&
            (advancedData is AdvancedData5G?) &&
            (data?.nbid?.let { it < 0 } == true)

    val (derivedCid, derivedNbid) = if (shouldDerive) {
        // For T-Mobile, gNB uses 24 bits (CID 12 bits)
        deriveCidGnbidFromEcgi((advancedData as AdvancedData5G?)?.ecgi, (advancedData as AdvancedData5G?)?.plmn, 24)
    } else {
        Pair(null, null)
    }

    val cidToShow = derivedCid ?: data?.cid
    val nbidToShow = derivedNbid ?: data?.nbid
    
    // Calculate SQSI for this RAT
    val sqsi = data?.let {
        val ratData = SQSICalculator.RatData(
            rsrp = it.rsrp,
            rsrq = it.rsrq,
            sinr = it.sinr,
            cqi = advancedData?.cqi,
            bandwidth = SQSICalculator.parseBandwidth(advancedData?.bandwidth)
        )
        
        // Calculate SQSI for this single RAT
        if (data is CellDataLTE?) {
            SQSICalculator.calculateSQSI(lteData = ratData, nrData = null)
        } else {
            SQSICalculator.calculateSQSI(lteData = null, nrData = ratData)
        }
    }

    val basicItems = generateInfoList(data, advancedData, mainData?.device) {
        // Add SQSI at the top
        sqsi?.let {
            val sqsiInt = it.toInt()
            val colorFraction = SQSICalculator.getSQSIColorFraction(it)
            // Map SQSI to color gradient (1-10 scale, where 10 is best)
            this[MR.strings.sqsi, MR.strings.sqsi_helper_text] = Triple(
                sqsiInt,
                1,  // Min value
                10  // Max value
            )
        }
        
        this[MR.strings.bands, MR.strings.bands_helper_text] = data?.bands?.bulletedList()
        this[MR.strings.rsrp, MR.strings.rsrp_helper_text] = Triple(data?.rsrp, -115, -77)
        this[MR.strings.rsrq, MR.strings.rsrq_helper_text] = Triple(data?.rsrq, -25, -9)
        this[MR.strings.rssi, MR.strings.rssi_helper_text] = Triple(data?.rssi, -95, -65)
        this[MR.strings.sinr, MR.strings.snr_helper_text] = Triple(data?.sinr, 2, 19)
        this[MR.strings.cid, MR.strings.cid_helper_text] = cidToShow?.toString()
        this[if (data is CellDataLTE?) MR.strings.enbid else MR.strings.gnbid, MR.strings.nbid_helper_text] = nbidToShow?.toString()
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
