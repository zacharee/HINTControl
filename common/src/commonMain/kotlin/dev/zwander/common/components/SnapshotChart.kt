package dev.zwander.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
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
import dev.zwander.common.util.Storage
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.LineChart
import io.github.koalaplot.core.line.Point
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xychart.LineStyle
import io.github.koalaplot.core.xychart.LinearAxisModel
import io.github.koalaplot.core.xychart.XYChart
import io.github.xxfast.kstore.extensions.updatesOrEmpty
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

private data class ChartData(
    val data: List<Point<Float, Float>>,
    val color: Color,
    val legendLabel: String,
)

@OptIn(ExperimentalObjCRefinement::class, ExperimentalKoalaPlotApi::class)
@HiddenFromObjC
@Composable
fun SnapshotChart(
    modifier: Modifier = Modifier,
) {
    val snapshots by Storage.snapshots.updatesOrEmpty.collectAsState(listOf())

    if (snapshots.isEmpty()) {
        return
    }

    val minX by remember {
        derivedStateOf {
            snapshots.minOf { (it.timeMillis / 1000) }
        }
    }
    val maxX by remember {
        derivedStateOf {
            snapshots.maxOf { (it.timeMillis / 1000) }
        }
    }

    val xAxisModel by remember {
        derivedStateOf {
            LinearAxisModel(
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
            val minY = snapshots.minOf { snapshot ->
                snapshot.mainData?.signal?.let { signal ->
                    val minFiveG = signal.fiveG?.let {
                        it.rsrp?.let { rsrp ->
                            it.rsrq?.let { rsrq ->
                                minOf(rsrp, rsrq)
                            }
                        }
                    }
                    val minFourG = signal.fourG?.let {
                        it.rsrp?.let { rsrp ->
                            it.rsrq?.let { rsrq ->
                                minOf(rsrp, rsrq)
                            }
                        }
                    }

                    minOf(minFiveG ?: Int.MAX_VALUE, minFourG ?: Int.MAX_VALUE)
                } ?: 0
            }

            val maxY = snapshots.maxOf { snapshot ->
                snapshot.mainData?.signal?.let { signal ->
                    val minFiveG = signal.fiveG?.let {
                        it.rsrp?.let { rsrp ->
                            it.rsrq?.let { rsrq ->
                                maxOf(rsrp, rsrq)
                            }
                        }
                    }
                    val minFourG = signal.fourG?.let {
                        it.rsrp?.let { rsrp ->
                            it.rsrq?.let { rsrq ->
                                maxOf(rsrp, rsrq)
                            }
                        }
                    }

                    maxOf(minFiveG ?: Int.MIN_VALUE, minFourG ?: Int.MIN_VALUE)
                } ?: 0
            }

            LinearAxisModel(
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
                    legendLabel = "LTE RSRP (dBm)",
                ),
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fiveG?.rsrp)
                    },
                    color = Color.Yellow,
                    legendLabel = "5G RSRP (dBm)",
                ),
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fourG?.rsrq)
                    },
                    color = Color.Red,
                    legendLabel = "LTE RSRQ (dB)",
                ),
                ChartData(
                    data = snapshots.mapNotNull { snapshot ->
                        createPoint(snapshot.timeMillis, snapshot.mainData?.signal?.fiveG?.rsrq)
                    },
                    color = Color.Magenta,
                    legendLabel = "5G RSRQ (dB)",
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
                        text = chartDataItems[it].legendLabel,
                        style = MaterialTheme.typography.labelSmall,
                    )
                },
            )
        },
        legendLocation = LegendLocation.BOTTOM,
        modifier = modifier.aspectRatio(1f),
    ) {
        XYChart(
            xAxisModel = xAxisModel,
            yAxisModel = yAxisModel,
            modifier = Modifier.height(300.dp),
            xAxisLabels = { "" },
            verticalMinorGridLineStyle = null,
            horizontalMinorGridLineStyle = null,
            panZoomEnabled = false,
        ) {
            chartDataItems.forEach { chartData ->
                LineChart(
                    data = chartData.data,
                    lineStyle = LineStyle(brush = SolidColor(chartData.color), strokeWidth = 1.dp),
                )
            }
        }
    }
}
