@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.data.HistoricalSnapshot
import dev.zwander.common.util.Storage
import dev.zwander.resources.common.MR
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun DataPage(
    modifier: Modifier = Modifier,
) {
    val snapshots by Storage.snapshots.updates.collectAsState(listOf())
    val rsrpRange = remember(snapshots) {
        calculateRsrpRange(snapshots)
    }
    val rsrqRange = remember(snapshots) {
        calculateRsrqRange(snapshots)
    }
    val times = remember(snapshots) {
        snapshots?.map { it.timeMillis } ?: listOf()
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        LineGraph(
            title = stringResource(MR.strings.rsrp),
            times = times,
            signalRange = rsrpRange,
            fourG = snapshots?.mapNotNull { snapshot ->
                snapshot.mainData?.signal?.fourG?.let {
                    snapshot.timeMillis to (it.rsrp?.toFloat() ?: 0f)
                }
            } ?: listOf(),
            fiveG = snapshots?.mapNotNull { snapshot ->
                snapshot.mainData?.signal?.fiveG?.let {
                    snapshot.timeMillis to (it.rsrp?.toFloat() ?: 0f)
                }
            } ?: listOf(),
            modifier = Modifier.fillMaxWidth(),
        )

        LineGraph(
            title = stringResource(MR.strings.rsrq),
            times = times,
            signalRange = rsrqRange,
            fourG = snapshots?.mapNotNull { snapshot ->
                snapshot.mainData?.signal?.fourG?.let {
                    snapshot.timeMillis to (it.rsrq?.toFloat() ?: 0f)
                }
            } ?: listOf(),
            fiveG = snapshots?.mapNotNull { snapshot ->
                snapshot.mainData?.signal?.fiveG?.let {
                    snapshot.timeMillis to (it.rsrq?.toFloat() ?: 0f)
                }
            } ?: listOf(),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun LineGraph(
    title: String,
    times: List<Long>,
    signalRange: ClosedFloatingPointRange<Float>,
    fourG: List<Pair<Long, Float>>,
    fiveG: List<Pair<Long, Float>>,
    modifier: Modifier = Modifier,
) {
    ChartLayout(
        modifier = modifier,
        title = { Text(title) },
        legend = { },
        legendLocation = LegendLocation.BOTTOM,
    ) {
        XYGraph(
            xAxisModel = CategoryAxisModel(times),
            yAxisModel = FloatLinearAxisModel(signalRange),
            xAxisLabels = {},
            xAxisTitle = {},
            yAxisLabels = {},
            yAxisTitle = {},
        ) {
            LinePlot(
                data = fourG.map { DefaultPoint(it.first, it.second) },
            )

            LinePlot(
                data = fiveG.map { DefaultPoint(it.first, it.second) },
            )
        }
    }
}

private fun calculateRsrpRange(snapshots: List<HistoricalSnapshot>?): ClosedFloatingPointRange<Float> {
    if (snapshots == null) {
        return 0f..100f
    }

    val maxRsrp = snapshots.maxOf {
        it.mainData?.signal?.let { signal ->
            maxOf(signal.fiveG?.rsrp ?: 0, signal.fourG?.rsrp ?: 0)
        } ?: 0
    }

    val minRsrp = snapshots.minOf {
        it.mainData?.signal?.let { signal ->
            minOf(signal.fiveG?.rsrp ?: 0, signal.fourG?.rsrp ?: 0)
        } ?: 0
    }

    return minRsrp.toFloat()..maxRsrp.toFloat()
}

private fun calculateRsrqRange(snapshots: List<HistoricalSnapshot>?): ClosedFloatingPointRange<Float> {
    if (snapshots == null) {
        return 0f..100f
    }

    val maxRsrq = snapshots.maxOf {
        it.mainData?.signal?.let { signal ->
            maxOf(signal.fiveG?.rsrq ?: 0, signal.fourG?.rsrq ?: 0)
        } ?: 0
    }

    val minRsrq = snapshots.minOf {
        it.mainData?.signal?.let { signal ->
            minOf(signal.fiveG?.rsrq ?: 0, signal.fourG?.rsrq ?: 0)
        } ?: 0
    }

    return minRsrq.toFloat()..maxRsrq.toFloat()
}
