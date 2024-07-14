package dev.zwander.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.util.Storage
import dev.zwander.common.util.nullableMaxOf
import dev.zwander.common.util.nullableMinOf
import dev.zwander.resources.common.MR
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.XYGraph
import korlibs.platform.Platform
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

private const val VERTICAL_AXIS_PADDING = 2

private data class ChartData(
    val data: List<Point<Float, Float>>,
    val color: Color,
    val legendLabel: StringResource,
)

@OptIn(ExperimentalObjCRefinement::class, ExperimentalKoalaPlotApi::class,
    ExperimentalMaterial3Api::class
)
@HiddenFromObjC
@Composable
fun SnapshotChart(
    modifier: Modifier = Modifier,
) {
    val fullSnapshots by Storage.snapshots.updates.collectAsState(listOf())
    val snapshots by remember {
        derivedStateOf {
            fullSnapshots?.run { slice((lastIndex - 60).coerceAtLeast(0)..lastIndex) } ?: listOf()
        }
    }

    if (snapshots.isEmpty()) {
        return
    }

    val minX by remember {
        derivedStateOf {
            (snapshots.firstOrNull()?.timeMillis ?: 0) / 1000
        }
    }
    val maxX by remember {
        derivedStateOf {
            (snapshots.lastOrNull()?.timeMillis ?: 0) / 1000
        }
    }

    val xAxisModel by remember {
        derivedStateOf {
            FloatLinearAxisModel(
                range = if (minX == maxX) {
                    0f..1f
                } else {
                    0f..(maxX - minX).toFloat()
                },
            )
        }
    }

    val yAxisModel by remember {
        derivedStateOf {
            val minY = snapshots.mapNotNull { snapshot ->
                nullableMinOf(
                    snapshot.mainData?.signal?.fiveG?.rsrp,
                    snapshot.mainData?.signal?.fiveG?.rsrq,
                    snapshot.mainData?.signal?.fiveG?.rssi,
                    snapshot.mainData?.signal?.fiveG?.sinr,
                    snapshot.mainData?.signal?.fourG?.rsrp,
                    snapshot.mainData?.signal?.fourG?.rsrq,
                    snapshot.mainData?.signal?.fourG?.rssi,
                    snapshot.mainData?.signal?.fourG?.sinr,
                )?.let { it - VERTICAL_AXIS_PADDING }
            }.minOrNull() ?: 0

            val maxY = snapshots.mapNotNull { snapshot ->
                nullableMaxOf(
                    snapshot.mainData?.signal?.fiveG?.rsrp,
                    snapshot.mainData?.signal?.fiveG?.rsrq,
                    snapshot.mainData?.signal?.fiveG?.rssi,
                    snapshot.mainData?.signal?.fiveG?.sinr,
                    snapshot.mainData?.signal?.fourG?.rsrp,
                    snapshot.mainData?.signal?.fourG?.rsrq,
                    snapshot.mainData?.signal?.fourG?.rssi,
                    snapshot.mainData?.signal?.fourG?.sinr,
                )?.let { it + VERTICAL_AXIS_PADDING }
            }.minOrNull() ?: 1

            FloatLinearAxisModel(
                range = if (minY == maxY) {
                    0f..1f
                } else {
                    minY.toFloat()..maxY.toFloat()
                },
            )
        }
    }

    val createPoint: (time: Long, value: Number?) -> Point<Float, Float>? = remember {
        { time, value ->
            if (value == null) {
                null
            } else {
                Point((time / 1000 - minX).toFloat(), value.toFloat())
            }
        }
    }

    val isLightText = LocalContentColor.current.luminance() > 0.5f
    val brightness = 0.8f

    val chartDataItems by remember {
        derivedStateOf {
            listOf(
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fiveG?.rsrp)
                    },
                    color = if (isLightText) Color(0xffbc8f8f) else Color(0xff3cb371),
                    legendLabel = MR.strings.chart_legend_5g_rsrp,
                ),
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fiveG?.rsrq)
                    },
                    color = if (isLightText) Color(0xff32cd32) else Color(0xff000080),
                    legendLabel = MR.strings.chart_legend_5g_rsrq,
                ),
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fiveG?.rssi)
                    },
                    color = if (isLightText) Color(0xffff4500) else Color(0xffbc8f8f),
                    legendLabel = MR.strings.chart_legend_5g_rssi,
                ),
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fiveG?.sinr)
                    },
                    color = if (isLightText) Color(0xffffd700) else Color(0xffb03060),
                    legendLabel = MR.strings.chart_legend_5g_sinr,
                ),
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fourG?.rsrp)
                    },
                    color = if (isLightText) Color(0xff00ffff) else Color(0xffff0000),
                    legendLabel = MR.strings.chart_legend_lte_rsrp,
                ),
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fourG?.rsrq)
                    },
                    color = if (isLightText) Color(0xffa020f0) else Color(0xff2e8b57),
                    legendLabel = MR.strings.chart_legend_lte_rsrq,
                ),
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fourG?.rssi)
                    },
                    color = if (isLightText) Color(0xff1e90ff) else Color(0xffb03060),
                    legendLabel = MR.strings.chart_legend_lte_rssi,
                ),
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fourG?.sinr)
                    },
                    color = if (isLightText) Color(0xffff1493) else Color(0xff00bfff),
                    legendLabel = MR.strings.chart_legend_lte_sinr,
                ),
            )
        }
    }

    ChartLayout(
        legend = {
            FlowLegend(
                itemCount = chartDataItems.size,
                symbol = {
                    Box(
                        modifier = Modifier.size(12.dp)
                            .background(chartDataItems[it].color),
                    )
                },
                label = {
                    Text(
                        text = stringResource(chartDataItems[it].legendLabel),
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
            )
        },
        legendLocation = LegendLocation.BOTTOM,
        modifier = modifier,
    ) {
        XYGraph(
            xAxisModel = xAxisModel,
            yAxisModel = yAxisModel,
            modifier = Modifier,
            xAxisLabels = { "" },
            verticalMinorGridLineStyle = null,
            horizontalMinorGridLineStyle = null,
            panZoomEnabled = false,
        ) {
            chartDataItems.forEach { chartData ->
                LinePlot(
                    data = chartData.data,
                    lineStyle = LineStyle(brush = SolidColor(chartData.color), strokeWidth = 1.dp),
                    symbol = {
                        TooltipBox(
                            positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(
                                spacingBetweenTooltipAndAnchor = 4.dp,
                            ),
                            state = rememberTooltipState(),
                            tooltip = {
                                PlainTooltip(caretSize = TooltipDefaults.caretSize) {
                                    Text(
                                        text = it.y.toString(),
                                        fontSize = 12.sp,
                                        lineHeight = 12.sp,
                                    )
                                }
                            },
                            enableUserInput = Platform.isAndroid || Platform.isIos,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(color = chartData.color, CircleShape)
                                    .hoverableElement {
                                        Box(
                                            modifier = Modifier
                                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                                .padding(4.dp),
                                            contentAlignment = Alignment.Center,
                                        ) {
                                            Text(
                                                text = it.y.toString(),
                                                fontSize = 12.sp,
                                                lineHeight = 12.sp,
                                            )
                                        }
                                    },
                            )
                        }
                    },
                )
            }
        }
    }
}
