package dev.zwander.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.model.adapters.BaseCellData
import dev.zwander.common.model.adapters.CellData5G
import dev.zwander.common.model.adapters.CellDataLTE
import dev.zwander.resources.common.MR

@Composable
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CellDataLayout(
    data: BaseCellData?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth(),
        ) {
            FormatText(
                text = stringResource(MR.strings.bands),
                textFormat = data?.bands?.joinToString(" • ").toString(),
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            FormatText(
                text = stringResource(MR.strings.rsrp),
                textFormat = data?.rsrp.toString(),
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            FormatText(
                text = stringResource(MR.strings.rsrq),
                textFormat = data?.rsrq.toString(),
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            FormatText(
                text = stringResource(MR.strings.rssi),
                textFormat = data?.rssi.toString(),
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            FormatText(
                text = stringResource(MR.strings.sinr),
                textFormat = data?.sinr.toString(),
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            FormatText(
                text = stringResource(MR.strings.cid),
                textFormat = data?.cid.toString(),
                modifier = Modifier.padding(horizontal = 4.dp),
            )

            if (data is CellDataLTE) {
                FormatText(
                    text = stringResource(MR.strings.enbid),
                    textFormat = data.eNBID.toString(),
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            } else if (data is CellData5G) {
                FormatText(
                    text = stringResource(MR.strings.gnbid),
                    textFormat = data.gNBID.toString(),
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
        }
    }
}
