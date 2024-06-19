@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.PageGrid
import dev.zwander.common.components.settings.About
import dev.zwander.common.components.settings.AboutTitleAddon
import dev.zwander.common.components.settings.AutoRefresh
import dev.zwander.common.components.settings.Credits
import dev.zwander.common.components.settings.DefaultPage
import dev.zwander.common.components.settings.RecordSnapshots
import dev.zwander.common.components.settings.Supporters
import dev.zwander.common.components.settings.WidgetRefresh
import dev.zwander.resources.common.MR
import korlibs.platform.Platform
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

private data class SettingsItem(
    val title: StringResource,
    val render: @Composable ColumnScope.() -> Unit,
    val titleAddon: (@Composable () -> Unit)? = null,
    val description: (@Composable () -> Unit)? = null,
)

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
                    AutoRefresh()
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
                    DefaultPage()
                },
                description = {
                    Text(
                        text = stringResource(MR.strings.default_page_desc),
                    )
                },
            ),
            SettingsItem(
                title = MR.strings.record_snapshots,
                render = {
                    RecordSnapshots()
                },
                description = {
                    Text(
                        text = stringResource(MR.strings.record_snapshots_desc)
                    )
                },
            ),
            SettingsItem(
                title = MR.strings.about,
                render = {
                    About()
                },
                titleAddon = {
                    AboutTitleAddon()
                },
            ),
            SettingsItem(
                title = MR.strings.supporters,
                render = {
                    Supporters()
                },
            ),
            SettingsItem(
                title = MR.strings.credits,
                render = {
                    Credits()
                },
            ),
        ) + if (Platform.isAndroid || Platform.isIos) {
            listOf(
                SettingsItem(
                    title = MR.strings.widget_refresh,
                    render = {
                        WidgetRefresh()
                    },
                ),
            )
        } else {
            listOf()
        }
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
            },
            itemIsSelectable = { false },
        )
    }
}
