@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.GradleConfig
import dev.zwander.common.components.DropdownMenuItem
import dev.zwander.common.components.LabeledDropdown
import dev.zwander.common.components.PageGrid
import dev.zwander.common.components.TextSwitch
import dev.zwander.common.data.Page
import dev.zwander.common.model.SettingsModel
import dev.zwander.common.util.PatreonSupportersParser
import dev.zwander.common.util.SupporterInfo
import dev.zwander.common.util.UrlHandler
import dev.zwander.common.util.animateContentHeight
import dev.zwander.resources.common.MR
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

private data class SettingsItem(
    val title: StringResource,
    val render: @Composable ColumnScope.() -> Unit,
    val titleAddon: (@Composable () -> Unit)? = null,
    val description: (@Composable () -> Unit)? = null,
)

private data class SocialIconData(
    val img: ImageResource,
    val link: String,
    val desc: StringResource,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
@HiddenFromObjC
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
                                enabled = tempPeriod.toLongOrNull().run { this != null && this != periodMs },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = stringResource(MR.strings.save),
                                )
                            }
                        },
                        label = {
                            Text(text = stringResource(MR.strings.period_ms))
                        },
                    )
                },
                description = {
                    Text(
                        text = stringResource(MR.strings.auto_refresh_desc),
                    )
                },
            ),
            SettingsItem(
                title = MR.strings.default_page,
                render = {
                    val validOptions = remember {
                        listOf(
                            Page.Main,
                            Page.Clients,
                            Page.Advanced,
                            Page.WifiConfig,
                        )
                    }

                    var selectedOption by SettingsModel.defaultPage.collectAsMutableState()
                    var menuExpanded by remember(validOptions) {
                        mutableStateOf(false)
                    }

                    LabeledDropdown(
                        label = stringResource(MR.strings.default_page),
                        expanded = menuExpanded,
                        onExpandChange = { menuExpanded = it },
                        modifier = Modifier.fillMaxWidth(),
                        selectedValue = selectedOption,
                        valueToString = { stringResource(selectedOption.titleRes) }
                    ) {
                        validOptions.forEach { option ->
                            DropdownMenuItem(
                                text = {
                                    Text(text = stringResource(option.titleRes))
                                },
                                onClick = {
                                    selectedOption = option
                                    menuExpanded = false
                                },
                            )
                        }
                    }
                },
                description = {
                    Text(
                        text = stringResource(MR.strings.default_page_desc),
                    )
                },
            ),
            SettingsItem(
                title = MR.strings.about,
                render = {
                    val socialIcons = remember {
                        listOf(
                            SocialIconData(
                                img = MR.images.github,
                                link = "https://github.com/zacharee/ArcadyanKVD21Control",
                                desc = MR.strings.github,
                            ),
                            SocialIconData(
                                img = MR.images.mastodon,
                                link = "https://androiddev.social/@wander1236",
                                desc = MR.strings.mastodon,
                            ),
                            SocialIconData(
                                img = MR.images.patreon,
                                link = "https://www.patreon.com/zacharywander",
                                desc = MR.strings.patreon,
                            ),
                            SocialIconData(
                                img = MR.images.web,
                                link = "https://zwander.dev",
                                desc = MR.strings.website,
                            ),
                        )
                    }

                    FlowRow(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = modifier,
                    ) {
                        socialIcons.forEach { (img, link, desc) ->
                            IconButton(
                                onClick = {
                                    UrlHandler.launchUrl(link)
                                },
                            ) {
                                Icon(
                                    painter = painterResource(img),
                                    contentDescription = stringResource(desc),
                                )
                            }
                        }
                    }
                },
                titleAddon = {
                    var clickCount by remember {
                        mutableStateOf(0)
                    }

                    LaunchedEffect(clickCount) {
                        if (clickCount >= 10) {
                            clickCount = 0
                            SettingsModel.fuzzerEnabled.value = !SettingsModel.fuzzerEnabled.value
                        }
                    }

                    Text(
                        text = GradleConfig.versionName,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.clickable(
                            enabled = true,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                clickCount++
                            },
                        )
                    )
                },
            ),
            SettingsItem(
                title = MR.strings.supporters,
                render = {
                    var supporters by remember {
                        mutableStateOf(listOf<SupporterInfo>())
                    }

                    LaunchedEffect(null) {
                        supporters = PatreonSupportersParser.getInstance().parseSupporters()
                    }

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 300.dp)
                            .animateContentHeight(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        items(items = supporters, key = { it.hashCode() }) { item ->
                            ElevatedCard(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    UrlHandler.launchUrl(item.link)
                                },
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxWidth()
                                        .heightIn(min = 56.dp),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Text(
                                        text = item.name,
                                    )
                                }
                            }
                        }
                    }
                },
            )
        )
    }

    Box(
        modifier = modifier,
    ) {
        PageGrid(
            items = items,
            renderItemTitle = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = stringResource(it.title),
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    it.titleAddon?.invoke()
                }
            },
            renderItem = {
                it.render(this)
            },
            modifier = Modifier.fillMaxSize(),
            renderItemDescription = {
                it.description?.invoke()
            }
        )
    }
}
