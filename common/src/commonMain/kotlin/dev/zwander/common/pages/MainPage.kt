package dev.zwander.common.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.CellBars
import dev.zwander.common.components.CellDataLayout
import dev.zwander.common.components.MainDataLayout
import dev.zwander.common.model.MainModel
import dev.zwander.common.model.UserModel
import dev.zwander.common.util.AdaptiveMod
import dev.zwander.common.util.SettingsManager
import dev.zwander.resources.common.MR

private data class ItemInfo(
    val title: StringResource,
    val render: @Composable (Modifier) -> Unit,
    val titleAccessory: (@Composable (Modifier) -> Unit)? = null,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainPage(
    modifier: Modifier = Modifier,
) {
    val data by MainModel.currentMainData.collectAsState()

    val items = remember(data) {
        listOf(
            ItemInfo(
                title = MR.strings.general,
                render = { MainDataLayout(it) },
            ),
            ItemInfo(
                title = MR.strings.lte,
                render = {
                    CellDataLayout(
                        data = data?.signal?.fourG,
                        modifier = it
                    )
                },
                titleAccessory = {
                    CellBars(
                        bars = data?.signal?.fourG?.bars?.toInt(),
                        modifier = it,
                    )
                },
            ),
            ItemInfo(
                title = MR.strings.five_g,
                render = {
                    CellDataLayout(
                        data = data?.signal?.fiveG,
                        modifier = it
                    )
                },
                titleAccessory = {
                    CellBars(
                        bars = data?.signal?.fiveG?.bars?.toInt(),
                        modifier = it,
                    )
                },
            )
        )
    }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyVerticalStaggeredGrid(
                contentPadding = PaddingValues(8.dp),
                columns = AdaptiveMod(300.dp, items.size - 1),
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalItemSpacing = 8.dp,
            ) {
                items(
                    items = items,
                    key = { it.title },
                    span = { if (it.title == MR.strings.general) StaggeredGridItemSpan.FullLine else StaggeredGridItemSpan.SingleLane }
                ) {
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth()
                                .padding(8.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Bottom,
                            ) {
                                Text(
                                    text = stringResource(it.title),
                                    style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.weight(1f),
                                )

                                it.titleAccessory?.invoke(Modifier.size(24.dp))
                            }

                            it.render(Modifier.fillMaxWidth())
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = {
                    UserModel.token.value = null
                    UserModel.username.value = "admin"
                    UserModel.password.value = null

                    SettingsManager.username = "admin"
                    SettingsManager.password = null
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp),
            ) {
                Text(
                    text = stringResource(MR.strings.log_out),
                )
            }
        }
    }
}
