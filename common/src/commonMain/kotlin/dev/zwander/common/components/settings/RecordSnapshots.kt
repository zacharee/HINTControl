package dev.zwander.common.components.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.TextSwitch
import dev.zwander.common.model.SettingsModel
import dev.zwander.common.util.FileExporter
import dev.zwander.common.util.Storage
import dev.zwander.resources.common.MR
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.io.files.FileNotFoundException
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.io.encodeToSink

@OptIn(ExperimentalSerializationApi::class)
@Composable
fun ColumnScope.RecordSnapshots() {
    val scope = rememberCoroutineScope()

    var enabled by SettingsModel.recordSnapshots.collectAsMutableState()

    val hasSnapshots = Storage.snapshotsDb.getDao().countAsFlow().collectAsState(0).value > 0

    TextSwitch(
        text = stringResource(MR.strings.enabled),
        checked = enabled,
        onCheckedChange = { enabled = it },
        modifier = Modifier.fillMaxWidth(),
    )

    Spacer(modifier = Modifier.size(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    try {
                        FileExporter.saveFile(Storage.NAME, false)?.use { output ->
                            output.write("[".toByteArray())
                            Storage.snapshotsDb.getDao().getIds().apply {
                                forEachIndexed { index, id ->
                                    val snapshot = Storage.snapshotsDb.getDao().getById(id)

                                    Storage.json.encodeToSink(snapshot, output)

                                    if (index < lastIndex) {
                                        output.write(",".toByteArray())
                                    }
                                }
                            }
                            output.write("]".toByteArray())
                        }
                    } catch (e: FileNotFoundException) {
                        e.printStackTrace()
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                    }
                }
            },
            modifier = Modifier.weight(1f),
            enabled = hasSnapshots,
        ) {
            Text(text = stringResource(MR.strings.export))
        }

        Button(
            onClick = {
                scope.launch {
                    Storage.snapshots.reset()
                    Storage.snapshotsDb.getDao().deleteAll()
                }
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
            ),
            enabled = hasSnapshots,
        ) {
            Text(text = stringResource(MR.strings.clear))
        }
    }
}
