package dev.zwander.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.dialog.AlertDialogDef
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.adapters.SSIDConfig
import dev.zwander.resources.common.MR

private data class SSIDItem(
    val name: String?,
    val data: SSIDConfig?,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SSIDListLayout(
    modifier: Modifier = Modifier,
) {
    var data by MainModel.tempWifiState.collectAsMutableState()

    val items by remember {
        derivedStateOf {
            data?.ssids?.map {
                SSIDItem(
                    name = it.ssidName,
                    data = it,
                )
            }
        }
    }

    var editingConfig by remember {
        mutableStateOf<Pair<SSIDConfig?, SSIDConfig?>?>(null)
    }

    var editingState by remember(editingConfig) {
        mutableStateOf(editingConfig?.second)
    }

    fun updateSsidConfig(old: SSIDConfig?, new: SSIDConfig?) {
        val newList = data?.ssids?.toMutableList() ?: mutableListOf()

        val index = if (old != null) newList.indexOf(old) else null

        if (index != null) {
            if (new != null) {
                newList[index] = new
            } else {
                newList.removeAt(index)
            }
        } else if (new != null) {
            newList.add(new)
        }

        data = data?.copy(
            ssids = newList,
        )
    }

    Column(
        modifier = modifier,
    ) {
        items?.forEachIndexed { index, item ->
            Row(
                modifier = Modifier.fillMaxWidth()
                    .heightIn(min = 56.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = item.name ?: "",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                )

                IconButton(
                    onClick = {
                        updateSsidConfig(item.data, null)
                    },
                    enabled = index > 0,
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(MR.strings.delete),
                    )
                }

                IconButton(
                    onClick = {
                        editingConfig = item.data to item.data?.copy()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = stringResource(MR.strings.edit),
                    )
                }
            }
        }

        OutlinedButton(
            onClick = {
                editingConfig = null to SSIDConfig(
                    twoGigSsid = true,
                    fiveGigSsid = true,
                    encryptionMode = "AES",
                    encryptionVersion = "WPA2/WPA3",
                    guest = false,
                    isBroadcastEnabled = true,
                    ssidName = null,
                    wpaKey = null,
                )
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = stringResource(MR.strings.add)
            )
        }
    }

    AlertDialogDef(
        showing = editingConfig != null,
        title = {
            Text(
                text = if (editingConfig?.first != null) {
                    stringResource(MR.strings.edit)
                } else {
                    stringResource(MR.strings.new_ssid)
                },
            )
        },
        text = {
            OutlinedTextField(
                value = editingState?.ssidName ?: "",
                onValueChange = {
                    editingState = editingState?.copy(
                        ssidName = it,
                    )
                },
                label = {
                    Text(
                        text = stringResource(MR.strings.ssid),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = editingState?.wpaKey ?: "",
                onValueChange = {
                    editingState = editingState?.copy(
                        wpaKey = it,
                    )
                },
                label = {
                    Text(
                        text = stringResource(MR.strings.password),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            )

            TextSwitch(
                text = stringResource(MR.strings.hidden),
                checked = editingState?.isBroadcastEnabled != true,
                onCheckedChange = {
                    editingState = editingState?.copy(
                        isBroadcastEnabled = !it,
                    )
                },
            )

            TextSwitch(
                text = stringResource(MR.strings.guest),
                checked = editingState?.guest == true,
                onCheckedChange = {
                    editingState = editingState?.copy(
                        guest = it,
                    )
                },
            )
        },
        onDismissRequest = { editingConfig = null },
        buttons = {
            TextButton(
                onClick = {
                    editingConfig = null
                }
            ) {
                Text(
                    text = stringResource(MR.strings.cancel),
                )
            }

            TextButton(
                onClick = {
                    updateSsidConfig(
                        old = editingConfig?.first,
                        new = editingState,
                    )

                    editingConfig = null
                },
            ) {
                Text(
                    text = stringResource(MR.strings.save),
                )
            }
        },
    )
}
