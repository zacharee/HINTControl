package dev.zwander.common.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.PageGrid
import dev.zwander.common.components.TextSwitch
import dev.zwander.common.model.SettingsModel
import dev.zwander.resources.common.MR

private data class SettingsItem(
    val title: StringResource,
    val render: @Composable ColumnScope.() -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsPage(
    modifier: Modifier = Modifier,
) {
    val items = remember {
        listOf(
            SettingsItem(
                title = MR.strings.auto_refresh,
                render = {
                    var enabled by SettingsModel.enableAutoRefresh.collectAsMutableState()
                    var periodMs by SettingsModel.autoRefreshMs.collectAsMutableState()

                    var tempPeriod by remember(periodMs) {
                        mutableStateOf(periodMs.toString())
                    }

                    TextSwitch(
                        text = stringResource(MR.strings.enabled),
                        checked = enabled,
                        onCheckedChange = { enabled = it },
                        modifier = Modifier.fillMaxWidth(),
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    OutlinedTextField(
                        value = tempPeriod,
                        onValueChange = {
                            tempPeriod = it.filter { char -> char.isDigit() }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    tempPeriod.toLongOrNull()?.let {
                                        periodMs = it
                                    }
                                },
                                enabled = tempPeriod.toLongOrNull() != null,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = stringResource(MR.strings.save),
                                )
                            }
                        },
                    )
                }
            )
        )
    }

    Box(
        modifier = modifier,
    ) {
        PageGrid(
            items = items,
            renderItemTitle = {
                Text(
                    text = stringResource(it.title),
                )
            },
            renderItem = {
                it.render(this)
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
