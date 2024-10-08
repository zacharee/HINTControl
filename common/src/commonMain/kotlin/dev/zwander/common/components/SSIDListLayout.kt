@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.adapters.EncryptionModes
import dev.zwander.common.model.adapters.EncryptionVersions
import dev.zwander.common.model.adapters.SSIDConfig
import dev.zwander.compose.alertdialog.InWindowAlertDialog
import dev.zwander.resources.common.MR
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

private data class SSIDItem(
    val name: String?,
    val data: SSIDConfig?,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
@HiddenFromObjC
fun SSIDListLayout(
    modifier: Modifier = Modifier,
) {
    var data by MainModel.tempWifiState.collectAsMutableState()

    val canAddAndRemove by remember {
        derivedStateOf {
            data?.canAddAndRemove
        }
    }

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

    val validWpaKey by remember(editingState) {
        derivedStateOf {
            editingState?.let { state ->
                state.wpaKey?.let { key ->
                    key.isNotBlank() &&
                            Regex("^[\\u0020-\\u007e]{8,63}\$").matches(key)
                }
            } ?: false
        }
    }

    fun updateSsidConfig(old: SSIDConfig?, new: SSIDConfig?) {
        val newList = data?.ssids?.toMutableList() ?: mutableListOf()

        val index = if (old != null) newList.indexOf(old) else null

        if (index != null && index != -1) {
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

                val freqs = remember(item) {
                    val list = mutableListOf<StringResource>()

                    if (item.data?.twoGigSsid == true) {
                        list.add(MR.strings.twoGig)
                    }

                    if (item.data?.fiveGigSsid == true) {
                        list.add(MR.strings.fiveGig)
                    }

                    list
                }

                @Suppress("SimplifiableCallChain")
                Text(
                    text = freqs.map { stringResource(it) }.joinToString("\n"),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 10.sp,
                )

                if (canAddAndRemove == true) {
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
                } else if (item.data?.enabled != null) {
                    Checkbox(
                        checked = item.data.enabled == true,
                        onCheckedChange = {
                            updateSsidConfig(
                                item.data,
                                item.data.copy(
                                    enabled = it,
                                )
                            )
                        },
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

        AnimatedVisibility(
            visible = canAddAndRemove == true,
        ) {
            Button(
                onClick = {
                    editingConfig = null to SSIDConfig(
                        twoGigSsid = true,
                        fiveGigSsid = true,
                        encryptionMode = "AES",
                        encryptionVersion = EncryptionVersions.wpa2Wpa3,
                        guest = false,
                        isBroadcastEnabled = true,
                        ssidName = null,
                        wpaKey = null,
                        enabled = null,
                        canEditFrequencyAndGuest = true,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(MR.strings.add)
                )
            }
        }
    }

    InWindowAlertDialog(
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
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
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
                    modifier = Modifier.weight(1f),
                    isError = editingState?.ssidName.isNullOrBlank(),
                    singleLine = true,
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
                    modifier = Modifier.weight(1f),
                    isError = !validWpaKey,
                    singleLine = true,
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            var encVExpanded by remember {
                mutableStateOf(false)
            }
            var encMExpanded by remember {
                mutableStateOf(false)
            }

            LabeledDropdown(
                label = stringResource(MR.strings.encryption),
                expanded = encVExpanded,
                onExpandChange = { encVExpanded = it },
                modifier = Modifier.fillMaxWidth(),
                selectedValue = editingState?.encryptionVersion ?: "",
            ) {
                val versions = remember {
                    listOf(
                        EncryptionVersions.wpaWpa2,
                        EncryptionVersions.wpa2,
                        EncryptionVersions.wpa2Wpa3,
                    )
                }

                versions.forEach { version ->
                    DropdownMenuItem(
                        text = {
                            Text(text = version)
                        },
                        onClick = {
                            editingState = editingState?.copy(
                                encryptionVersion = version,
                            )
                            encVExpanded = false
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            LabeledDropdown(
                label = stringResource(MR.strings.encryption_mode),
                expanded = encMExpanded,
                onExpandChange = { encMExpanded = it },
                modifier = Modifier.fillMaxWidth(),
                selectedValue = editingState?.encryptionMode ?: "",
            ) {
                val versions = remember {
                    listOf(
                        EncryptionModes.aes,
                        EncryptionModes.tkip,
                    )
                }

                versions.forEach { version ->
                    DropdownMenuItem(
                        text = {
                            Text(text = version)
                        },
                        onClick = {
                            editingState = editingState?.copy(
                                encryptionMode = version,
                            )
                            encMExpanded = false
                        },
                    )
                }
            }

            Spacer(modifier = Modifier.size(8.dp))

            AnimatedVisibility(
                visible = editingConfig?.second?.enabled != null,
            ) {
                Column {
                    TextSwitch(
                        text = stringResource(MR.strings.enabled),
                        checked = editingState?.enabled == true,
                        onCheckedChange = {
                            editingState = editingState?.copy(
                                enabled = it,
                            )
                        },
                    )

                    Spacer(modifier = Modifier.size(8.dp))
                }
            }

            TextSwitch(
                text = stringResource(MR.strings.hidden),
                checked = editingState?.isBroadcastEnabled == false,
                onCheckedChange = {
                    editingState = editingState?.copy(
                        isBroadcastEnabled = !it,
                    )
                },
            )

            Spacer(modifier = Modifier.size(8.dp))

            AnimatedVisibility(
                visible = editingConfig?.second?.canEditFrequencyAndGuest == true,
            ) {
                Column {
                    TextSwitch(
                        text = stringResource(MR.strings.guest),
                        checked = editingState?.guest == true,
                        onCheckedChange = {
                            editingState = editingState?.copy(
                                guest = it,
                            )
                        },
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    TextSwitch(
                        text = stringResource(MR.strings.twoGig),
                        checked = editingState?.twoGigSsid == true,
                        onCheckedChange = {
                            editingState = editingState?.copy(
                                twoGigSsid = it,
                                fiveGigSsid = if (!it) true else editingState?.fiveGigSsid,
                            )
                        },
                        enabled = editingState?.fiveGigSsid == true,
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    TextSwitch(
                        text = stringResource(MR.strings.fiveGig),
                        checked = editingState?.fiveGigSsid == true,
                        onCheckedChange = {
                            editingState = editingState?.copy(
                                fiveGigSsid = it,
                                twoGigSsid = if (!it) true else editingState?.twoGigSsid,
                            )
                        },
                        enabled = editingState?.twoGigSsid == true,
                    )
                }
            }
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
                enabled = !editingState?.ssidName.isNullOrBlank() &&
                        validWpaKey,
            ) {
                Text(
                    text = stringResource(MR.strings.save),
                )
            }
        },
    )
}
