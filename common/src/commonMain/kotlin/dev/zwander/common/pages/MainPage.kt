package dev.zwander.common.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.CellDataLayout
import dev.zwander.common.components.MainDataLayout
import dev.zwander.common.model.MainModel
import dev.zwander.common.util.AdaptiveMod
import dev.zwander.resources.common.MR

private data class ItemInfo(
    val title: StringResource,
    val render: @Composable (Modifier) -> Unit,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainPage(
    modifier: Modifier = Modifier,
) {
    val data by MainModel.currentMainData.collectAsState()

    val items = remember {
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
            ),
            ItemInfo(
                title = MR.strings.five_g,
                render = {
                    CellDataLayout(
                        data = data?.signal?.fiveG,
                        modifier = it
                    )
                },
            )
        )
    }

    Box(
        modifier = modifier
    ) {
        LazyVerticalStaggeredGrid(
            contentPadding = PaddingValues(8.dp),
            columns = AdaptiveMod(300.dp, items.size - 1),
            modifier = Modifier.fillMaxSize(),
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
                        Text(
                            text = stringResource(it.title),
                            style = MaterialTheme.typography.titleMedium,
                        )

                        it.render(Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}
