package dev.zwander.common.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.zwander.common.util.AdaptiveMod

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> PageGrid(
    items: List<T>,
    key: (T) -> Any = { it.hashCode() },
    renderItem: @Composable ColumnScope.(T) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalStaggeredGrid(
        contentPadding = PaddingValues(8.dp),
        columns = AdaptiveMod(300.dp, items.size),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
    ) {
        items(items = items, key = key) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(8.dp),
                ) {
                    renderItem(it)
                }
            }
        }
    }
}
