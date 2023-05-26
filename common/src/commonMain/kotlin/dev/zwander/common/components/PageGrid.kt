@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.zwander.common.util.AdaptiveMod
import dev.zwander.common.util.animateContentHeight
import dev.zwander.common.util.animateContentWidth
import korlibs.memory.Platform
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalFoundationApi::class)
@Composable
@HiddenFromObjC
fun <T> PageGrid(
    items: List<T>,
    key: (T) -> Any = { it.hashCode() },
    renderItemTitle: @Composable ColumnScope.(T) -> Unit,
    renderItem: @Composable ColumnScope.(T) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalStaggeredGrid(
        contentPadding = PaddingValues(8.dp),
        columns = AdaptiveMod(250.dp, items.size),
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalItemSpacing = 8.dp,
    ) {
        items(items = items, key = key) {
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .animateContentHeight()
                        .then(if (Platform.isAndroid || Platform.isIos) {
                            Modifier.animateContentWidth()
                        } else {
                            Modifier
                        })
                        .fillMaxWidth()
                        .padding(8.dp),
                ) {
                    CompositionLocalProvider(
                        LocalTextStyle provides MaterialTheme.typography.titleLarge,
                    ) {
                        renderItemTitle(it)
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    renderItem(it)
                }
            }
        }
    }
}
