package dev.zwander.common.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.adapters.CellData
import dev.zwander.resources.common.MR

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainDataLayout(
    modifier: Modifier = Modifier
) {
    val data by MainModel.currentMainData.collectAsState()

    BoxWithConstraints(
        modifier = modifier,
    ) {
        val constraints = this.constraints
        val maxWidthDp = with (LocalDensity.current) { constraints.maxWidth.toDp() }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(MR.strings.general),
                    style = MaterialTheme.typography.titleMedium,
                )

                FlowRow(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    FormatText(
                        text = stringResource(MR.strings.apn),
                        textFormat = data?.signal?.generic?.apn.toString(),
                    )

                    FormatText(
                        text = stringResource(MR.strings.ipv6),
                        textFormat = data?.signal?.generic?.hasIPv6.toString(),
                    )

                    FormatText(
                        text = stringResource(MR.strings.registration),
                        textFormat = data?.signal?.generic?.registration.toString(),
                    )

                    FormatText(
                        text = stringResource(MR.strings.roaming),
                        textFormat = data?.signal?.generic?.registration.toString(),
                    )
                }
            }

            Divider()

            Crossfade(
                targetState = maxWidthDp > 400.dp,
            ) {
                if (it) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(32.dp),
                    ) {
                        CellInfo(
                            title = stringResource(MR.strings.lte),
                            data = data?.signal?.fourG,
                            modifier = Modifier.weight(1f),
                        )

                        CellInfo(
                            title = stringResource(MR.strings.five_g),
                            data = data?.signal?.fiveG,
                            modifier = Modifier.weight(1f),
                        )
                    }
                } else {
                    Column {
                        CellInfo(
                            title = stringResource(MR.strings.lte),
                            data = data?.signal?.fourG,
                        )

                        Divider()

                        CellInfo(
                            title = stringResource(MR.strings.five_g),
                            data = data?.signal?.fiveG,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CellInfo(
    title: String,
    data: CellData?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )

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
