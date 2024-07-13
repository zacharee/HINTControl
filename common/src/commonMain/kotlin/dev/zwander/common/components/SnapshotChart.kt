package dev.zwander.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.util.Storage
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
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

private const val VERTICAL_AXIS_PADDING = 2

private data class ChartData(
    val data: List<Point<Float, Float>>,
    val color: Color,
    val legendLabel: StringResource,
)

@OptIn(ExperimentalObjCRefinement::class, ExperimentalKoalaPlotApi::class)
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
                snapshot.mainData?.signal?.let { signal ->
                    val minFiveG = signal.fiveG?.let {
                        it.rsrp?.let { rsrp ->
                            it.rsrq?.let { rsrq ->
                                minOf(rsrp, rsrq) - VERTICAL_AXIS_PADDING
                            }
                        }
                    }
                    val minFourG = signal.fourG?.let {
                        it.rsrp?.let { rsrp ->
                            it.rsrq?.let { rsrq ->
                                minOf(rsrp, rsrq) - VERTICAL_AXIS_PADDING
                            }
                        }
                    }

                    if (minFiveG == null && minFourG != null) {
                        minFourG
                    } else if (minFiveG != null && minFourG == null) {
                        minFiveG
                    } else if (minFiveG != null && minFourG != null) {
                        minOf(minFourG, minFiveG)
                    } else {
                        null
                    }
                }
            }.minOrNull() ?: 0

            val maxY = snapshots.mapNotNull { snapshot ->
                snapshot.mainData?.signal?.let { signal ->
                    val maxFiveG = signal.fiveG?.let {
                        it.rsrp?.let { rsrp ->
                            it.rsrq?.let { rsrq ->
                                maxOf(rsrp, rsrq) + VERTICAL_AXIS_PADDING
                            }
                        }
                    }
                    val maxFourG = signal.fourG?.let {
                        it.rsrp?.let { rsrp ->
                            it.rsrq?.let { rsrq ->
                                maxOf(rsrp, rsrq) + VERTICAL_AXIS_PADDING
                            }
                        }
                    }

                    if (maxFiveG == null && maxFourG != null) {
                        maxFourG
                    } else if (maxFiveG != null && maxFourG == null) {
                        maxFiveG
                    } else if (maxFiveG != null && maxFourG != null) {
                        maxOf(maxFourG, maxFiveG)
                    } else {
                        null
                    }
                }
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

    val chartDataItems by remember {
        derivedStateOf {
            listOf(
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fourG?.rsrp)
                    },
                    color = Color.Green,
                    legendLabel = MR.strings.chart_legend_lte_rsrp,
                ),
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fiveG?.rsrp)
                    },
                    color = Color.Yellow,
                    legendLabel = MR.strings.chart_legend_5g_rsrp,
                ),
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fourG?.rsrq)
                    },
                    color = Color.Red,
                    legendLabel = MR.strings.chart_legend_lte_rsrq,
                ),
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fiveG?.rsrq)
                    },
                    color = Color.Magenta,
                    legendLabel = MR.strings.chart_legend_5g_rsrq,
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
                )
            }
        }
    }
}
