@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.data.InfoItem
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.SavedDataEntry
import dev.zwander.common.util.SQSICalculator
import dev.zwander.resources.common.MR
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC
import kotlin.time.ExperimentalTime

@Composable
@HiddenFromObjC
fun SavedDataPage(
    modifier: Modifier = Modifier,
) {
    val savedDataEntries by MainModel.savedDataEntries.collectAsState()
    
    Column(
        modifier = modifier.padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(MR.strings.saved_data),
                style = MaterialTheme.typography.headlineMedium,
            )
            
            Button(
                onClick = { MainModel.clearSavedData() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                ),
                enabled = savedDataEntries.isNotEmpty(),
            ) {
                Text(stringResource(MR.strings.clear_saved))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (savedDataEntries.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Text(
                    text = "No saved data yet. Navigate to the Main page and tap Save to capture current data.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(savedDataEntries.reversed()) { entry ->
                    SavedDataCard(entry)
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class, ExperimentalLayoutApi::class)
@Composable
private fun SavedDataCard(
    entry: SavedDataEntry,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            val dateTime = kotlin.time.Instant.fromEpochMilliseconds(entry.timestamp)
            Text(
                text = "Saved at: ${dateTime}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            entry.mainData?.signal?.let { signal ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    signal.fourG?.let { lte ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "LTE",
                                style = MaterialTheme.typography.labelLarge,
                            )
                            
                            // Calculate and display SQSI
                            val lteSqsi = SQSICalculator.calculateSQSI(
                                lteData = SQSICalculator.RatData(
                                    rsrp = lte.rsrp,
                                    rsrq = lte.rsrq,
                                    sinr = lte.sinr,
                                    cqi = entry.cellData?.cell?.fourG?.cqi,
                                    bandwidth = SQSICalculator.parseBandwidth(entry.cellData?.cell?.fourG?.bandwidth)
                                ),
                                nrData = null
                            )
                            
                            lteSqsi?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                val item = InfoItem.ColorGradientItem(
                                    label = MR.strings.sqsi,
                                    value = it.toInt(),
                                    details = null,
                                    minValue = 1,
                                    maxValue = 10
                                )
                                item.Render(Modifier.padding(horizontal = 4.dp))
                            }
                            
                            FlowRow(
                                horizontalArrangement = Arrangement.Center,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                // Color-coded signal values
                                lte.rsrp?.let {
                                    val item = InfoItem.ColorGradientItem(
                                        label = MR.strings.rsrp,
                                        value = it,
                                        details = null,
                                        minValue = -115,
                                        maxValue = -77
                                    )
                                    item.Render(Modifier.padding(horizontal = 4.dp))
                                }
                                
                                lte.rsrq?.let {
                                    val item = InfoItem.ColorGradientItem(
                                        label = MR.strings.rsrq,
                                        value = it,
                                        details = null,
                                        minValue = -25,
                                        maxValue = -9
                                    )
                                    item.Render(Modifier.padding(horizontal = 4.dp))
                                }
                                
                                lte.sinr?.let {
                                    val item = InfoItem.ColorGradientItem(
                                        label = MR.strings.sinr,
                                        value = it,
                                        details = null,
                                        minValue = 2,
                                        maxValue = 19
                                    )
                                    item.Render(Modifier.padding(horizontal = 4.dp))
                                }
                                
                                lte.rssi?.let {
                                    val item = InfoItem.ColorGradientItem(
                                        label = MR.strings.rssi,
                                        value = it,
                                        details = null,
                                        minValue = -95,
                                        maxValue = -65
                                    )
                                    item.Render(Modifier.padding(horizontal = 4.dp))
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // CQI from cellData if available
                            entry.cellData?.cell?.fourG?.cqi?.let {
                                val item = InfoItem.ColorGradientItem(
                                    label = MR.strings.cqi,
                                    value = it,
                                    details = null,
                                    minValue = 0,
                                    maxValue = 12
                                )
                                item.Render(Modifier.padding(horizontal = 4.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            
                            Text("eNBID: ${lte.nbid ?: "--"}", style = MaterialTheme.typography.bodyMedium)
                            Text("CID: ${lte.cid ?: "--"}", style = MaterialTheme.typography.bodyMedium)
                            Text("Bands: ${lte.bands?.joinToString() ?: "--"}", style = MaterialTheme.typography.bodyMedium)
                            lte.antennaUsed?.let {
                                Text("Antenna: $it", style = MaterialTheme.typography.bodyMedium)
                            }
                            entry.cellData?.cell?.fourG?.bandwidth?.let {
                                Text("Bandwidth: $it", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                    
                    signal.fiveG?.let { fiveG ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = "5G",
                                style = MaterialTheme.typography.labelLarge,
                            )
                            
                            // Calculate and display SQSI
                            val nrSqsi = SQSICalculator.calculateSQSI(
                                lteData = null,
                                nrData = SQSICalculator.RatData(
                                    rsrp = fiveG.rsrp,
                                    rsrq = fiveG.rsrq,
                                    sinr = fiveG.sinr,
                                    cqi = entry.cellData?.cell?.fiveG?.cqi,
                                    bandwidth = SQSICalculator.parseBandwidth(entry.cellData?.cell?.fiveG?.bandwidth)
                                )
                            )
                            
                            nrSqsi?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                val item = InfoItem.ColorGradientItem(
                                    label = MR.strings.sqsi,
                                    value = it.toInt(),
                                    details = null,
                                    minValue = 1,
                                    maxValue = 10
                                )
                                item.Render(Modifier.padding(horizontal = 4.dp))
                            }
                            
                            FlowRow(
                                horizontalArrangement = Arrangement.Center,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                // Color-coded signal values
                                fiveG.rsrp?.let {
                                    val item = InfoItem.ColorGradientItem(
                                        label = MR.strings.rsrp,
                                        value = it,
                                        details = null,
                                        minValue = -115,
                                        maxValue = -77
                                    )
                                    item.Render(Modifier.padding(horizontal = 4.dp))
                                }
                                
                                fiveG.rsrq?.let {
                                    val item = InfoItem.ColorGradientItem(
                                        label = MR.strings.rsrq,
                                        value = it,
                                        details = null,
                                        minValue = -25,
                                        maxValue = -9
                                    )
                                    item.Render(Modifier.padding(horizontal = 4.dp))
                                }
                                
                                fiveG.sinr?.let {
                                    val item = InfoItem.ColorGradientItem(
                                        label = MR.strings.sinr,
                                        value = it,
                                        details = null,
                                        minValue = 2,
                                        maxValue = 19
                                    )
                                    item.Render(Modifier.padding(horizontal = 4.dp))
                                }
                                
                                fiveG.rssi?.let {
                                    val item = InfoItem.ColorGradientItem(
                                        label = MR.strings.rssi,
                                        value = it,
                                        details = null,
                                        minValue = -95,
                                        maxValue = -65
                                    )
                                    item.Render(Modifier.padding(horizontal = 4.dp))
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            // CQI from cellData if available
                            entry.cellData?.cell?.fiveG?.cqi?.let {
                                val item = InfoItem.ColorGradientItem(
                                    label = MR.strings.cqi,
                                    value = it,
                                    details = null,
                                    minValue = 0,
                                    maxValue = 12
                                )
                                item.Render(Modifier.padding(horizontal = 4.dp))
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            
                            Text("gNBID: ${fiveG.nbid ?: "--"}", style = MaterialTheme.typography.bodyMedium)
                            Text("CID: ${fiveG.cid ?: "--"}", style = MaterialTheme.typography.bodyMedium)
                            Text("Bands: ${fiveG.bands?.joinToString() ?: "--"}", style = MaterialTheme.typography.bodyMedium)
                            fiveG.antennaUsed?.let {
                                Text("Antenna: $it", style = MaterialTheme.typography.bodyMedium)
                            }
                            entry.cellData?.cell?.fiveG?.bandwidth?.let {
                                Text("Bandwidth: $it", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}