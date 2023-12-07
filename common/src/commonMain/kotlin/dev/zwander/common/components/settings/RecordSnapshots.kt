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
import io.github.xxfast.kstore.file.utils.FILE_SYSTEM
import korlibs.memory.Platform
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import okio.FileNotFoundException
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

@Composable
fun ColumnScope.RecordSnapshots() {
    val scope = rememberCoroutineScope()
    val sinkCreator = FileExporter.rememberBufferedSinkCreator()

    var enabled by SettingsModel.recordSnapshots.collectAsMutableState()

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
        if (!Platform.isIos) {
            Button(
                onClick = {
                    scope.launch(Dispatchers.IO) {
                        try {
                            FILE_SYSTEM.source(Storage.path.toPath()).buffer().use { input ->
                                sinkCreator(Storage.NAME)?.use { output ->
                                    output.writeAll(input)
                                }
                            }
                        } catch (e: FileNotFoundException) {
                            e.printStackTrace()
                        } catch (e: NullPointerException) {
                            e.printStackTrace()
                        }
                    }
                },
                modifier = Modifier.weight(1f),
            ) {
                Text(text = stringResource(MR.strings.export))
            }
        }

        Button(
            onClick = {
                scope.launch {
                    Storage.snapshots.reset()
                }
            },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
            ),
        ) {
            Text(text = stringResource(MR.strings.clear))
        }
    }
}
