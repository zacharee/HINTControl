package dev.zwander.common.components.settings

import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.util.UpdateUtil
import dev.zwander.resources.common.MR
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch

@Composable
fun Updater(
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var availableVersion by rememberSaveable {
        mutableStateOf<String?>(null)
    }
    var loading by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(
            modifier = Modifier.weight(1f)
                .heightIn(min = 48.dp)
                .align(Alignment.CenterVertically),
            contentAlignment = Alignment.CenterStart,
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = loading,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = !loading && availableVersion != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Text(
                    text = availableVersion
                        ?.takeIf { version -> version.isNotBlank() }
                        ?.let { version -> stringResource(MR.strings.update_available, version) }
                        ?: stringResource(MR.strings.no_updates_available),
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = !loading && availableVersion == null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Text(
                    text = stringResource(MR.strings.check_for_updates),
                )
            }
        }

        Crossfade(
            modifier = Modifier.align(Alignment.CenterVertically),
            targetState = availableVersion?.isNotBlank() == true,
        ) { updateAvailable ->
            Box(
                contentAlignment = Alignment.CenterEnd,
            ) {
                if (updateAvailable) {
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                loading = true
                                UpdateUtil.installUpdate()
                                loading = false
                            }
                        },
                        enabled = !loading,
                    ) {
                        Text(text = stringResource(MR.strings.update))
                    }
                } else {
                    Button(
                        onClick = {
                            scope.launch(Dispatchers.IO) {
                                loading = true
                                availableVersion = UpdateUtil.checkForUpdate()?.newVersion ?: ""
                                loading = false
                            }
                        },
                        enabled = !loading,
                    ) {
                        Text(text = stringResource(MR.strings.check))
                    }
                }
            }
        }
    }
}
