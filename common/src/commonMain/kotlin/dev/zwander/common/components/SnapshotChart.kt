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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.data.HistoricalSnapshot
import dev.zwander.common.util.Storage
import dev.zwander.common.util.invoke
import dev.zwander.common.util.nullableMaxOf
import dev.zwander.common.util.nullableMinOf
import dev.zwander.resources.common.MR
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.IntLinearAxisModel
import io.github.koalaplot.core.xygraph.LongLinearAxisModel
import io.github.koalaplot.core.xygraph.Point
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.XYGraphScope
import io.github.koalaplot.core.xygraph.rememberAxisStyle
import korlibs.platform.Platform
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

private const val VERTICAL_AXIS_PADDING = 2

private data class ChartData(
    val data: List<Point<Long, Int>>,
    val color: Color,
    val legendLabel: StringResource,
) {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun XYGraphScope<Long, Int>.Plot() {
        LinePlot(
            data = data,
            lineStyle = LineStyle(brush = SolidColor(color), strokeWidth = 1.dp),
            symbol = {
                TooltipBox(
                    positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                        spacingBetweenTooltipAndAnchor = 4.dp
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
                            .background(color = color, CircleShape)
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

private fun createPoint(time: Long, value: Number?, minX: Long): Point<Long, Int>? {
    return if (value == null) {
        null
    } else {
        Point((time - minX), value.toInt())
    }
}

@OptIn(
    ExperimentalObjCRefinement::class, ExperimentalKoalaPlotApi::class,
)
@HiddenFromObjC
@Composable
fun SnapshotChart(
    modifier: Modifier = Modifier,
) {
    val isLightText = LocalContentColor.current.luminance() > 0.5f
    val fullSnapshots by Storage.snapshots.updates.collectAsState(listOf())

    if (fullSnapshots.isNullOrEmpty()) {
        return
    }

    var snapshots by remember {
        mutableStateOf(listOf<HistoricalSnapshot>())
    }
    var minX by remember {
        mutableStateOf(0L)
    }
    var maxX by remember {
        mutableStateOf(1L)
    }
    var minY by remember {
        mutableStateOf(0)
    }
    var maxY by remember {
        mutableStateOf(1)
    }
    val chartDataItems = remember {
        mutableStateMapOf<Int, ChartData>()
    }

    LaunchedEffect(fullSnapshots) {
        val currentTime = Clock.System.now().toEpochMilliseconds()
        val newSnapshots = fullSnapshots?.sortedBy { it.timeMillis }?.run {
            val firstIndex = indexOfFirst { it.timeMillis >= (currentTime - (60 * 1000)) }.takeIf { it != -1 }

            firstIndex?.let { slice(firstIndex..lastIndex) } ?: takeLast(10)
        } ?: listOf()

        minX = (newSnapshots.minOfOrNull { it.timeMillis } ?: 0)
        maxX = (newSnapshots.maxOfOrNull { it.timeMillis } ?: 0)
        minY = newSnapshots.mapNotNull { snapshot ->
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
        maxY = newSnapshots.mapNotNull { snapshot ->
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
        }.maxOrNull() ?: 1

        snapshots = newSnapshots

        chartDataItems[0] = ChartData(
            data = newSnapshots.mapNotNull { snapshot ->
                createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fiveG?.rsrp, minX)
            },
            color = if (isLightText) Color(0xffbc8f8f) else Color(0xff3cb371),
            legendLabel = MR.strings.chart_legend_5g_rsrp,
        )
        chartDataItems[1] = ChartData(
            data = snapshots.mapNotNull { snapshot ->
                createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fiveG?.rsrq, minX)
            },
            color = if (isLightText) Color(0xff32cd32) else Color(0xff000080),
            legendLabel = MR.strings.chart_legend_5g_rsrq,
        )
        chartDataItems[2] = ChartData(
            data = snapshots.mapNotNull { snapshot ->
                createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fiveG?.rssi, minX)
            },
            color = if (isLightText) Color(0xffff4500) else Color(0xffbc8f8f),
            legendLabel = MR.strings.chart_legend_5g_rssi,
        )
        chartDataItems[3] = ChartData(
            data = snapshots.mapNotNull { snapshot ->
                createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fiveG?.sinr, minX)
            },
            color = if (isLightText) Color(0xffffd700) else Color(0xffb03060),
            legendLabel = MR.strings.chart_legend_5g_sinr,
        )
        chartDataItems[4] = ChartData(
            data = snapshots.mapNotNull { snapshot ->
                createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fourG?.rsrp, minX)
            },
            color = if (isLightText) Color(0xff00ffff) else Color(0xffff0000),
            legendLabel = MR.strings.chart_legend_lte_rsrp,
        )
        chartDataItems[5] = ChartData(
            data = snapshots.mapNotNull { snapshot ->
                createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fourG?.rsrq, minX)
            },
            color = if (isLightText) Color(0xffa020f0) else Color(0xff2e8b57),
            legendLabel = MR.strings.chart_legend_lte_rsrq,
        )
        chartDataItems[6] = ChartData(
            data = snapshots.mapNotNull { snapshot ->
                createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fourG?.rssi, minX)
            },
            color = if (isLightText) Color(0xff1e90ff) else Color(0xffb03060),
            legendLabel = MR.strings.chart_legend_lte_rssi,
        )
        chartDataItems[7] = ChartData(
            data = snapshots.mapNotNull { snapshot ->
                createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fourG?.sinr, minX)
            },
            color = if (isLightText) Color(0xffff1493) else Color(0xff00bfff),
            legendLabel = MR.strings.chart_legend_lte_sinr,
        )
    }

    if (snapshots.isEmpty()) {
        return
    }

    val maxXDateTime = Instant.fromEpochMilliseconds(maxX)

    val xAxisModel by remember {
        derivedStateOf {
            LongLinearAxisModel(
                range = if (minX == maxX) {
                    0L..1L
                } else {
                    0L..(maxX - minX)
                },
            )
        }
    }

    val yAxisModel by remember {
        derivedStateOf {
            IntLinearAxisModel(
                range = if (minY == maxY) {
                    0..1
                } else {
                    minY..maxY
                },
            )
        }
    }

    ChartLayout(
        legend = {
            FlowLegend(
                itemCount = chartDataItems.size,
                symbol = {
                    chartDataItems[it]?.color?.let { color ->
                        Box(
                            modifier = Modifier.size(12.dp)
                                .background(color),
                        )
                    }
                },
                label = {
                    chartDataItems[it]?.legendLabel?.let { label ->
                        Text(
                            text = stringResource(label),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    }
                },
                modifier = Modifier.padding(top = 8.dp),
            )
        },
        legendLocation = LegendLocation.BOTTOM,
        modifier = modifier,
    ) {
        XYGraph(
            xAxisModel = xAxisModel,
            yAxisModel = yAxisModel,
            xAxisLabels = {
                val seconds = (maxXDateTime - Instant.fromEpochMilliseconds(it + minX)).inWholeSeconds
                Text(
                    text = MR.strings.graph_seconds_format(seconds),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                    ),
                    modifier = Modifier.padding(top = 2.dp),
                )
            },
            yAxisLabels = {
                Text(
                    text = it.toString(),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 12.sp,
                    ),
                )
            },
            verticalMinorGridLineStyle = null,
            horizontalMinorGridLineStyle = null,
            yAxisStyle = rememberAxisStyle(labelRotation = 90),
            content = {
                chartDataItems.values.forEach { item ->
                    with(item) { Plot() }
                }
            },
            xAxisTitle = {},
        )
    }
}
