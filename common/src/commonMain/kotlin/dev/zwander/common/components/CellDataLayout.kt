package dev.zwander.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.model.adapters.CellData
import dev.zwander.resources.common.MR

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CellDataLayout(
    data: CellData?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(
                painter = painterResource(
                    when (data?.bars?.toInt()) {
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

        FlowRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            FormatText(
                text = stringResource(MR.strings.bands),
                textFormat = data?.bands?.joinToString(" • ").toString()
            )

            FormatText(
                text = stringResource(MR.strings.rsrp),
                textFormat = data?.rsrp.toString()
            )

            FormatText(
                text = stringResource(MR.strings.rsrq),
                textFormat = data?.rsrp.toString()
            )

            FormatText(
                text = stringResource(MR.strings.rssi),
                textFormat = data?.rssi.toString()
            )

            FormatText(
                text = stringResource(MR.strings.sinr),
                textFormat = data?.sinr.toString()
            )

            FormatText(
                text = stringResource(MR.strings.enbid),
                textFormat = data?.eNBID.toString()
            )

            FormatText(
                text = stringResource(MR.strings.cid),
                textFormat = data?.cid.toString()
            )
        }
    }
}
