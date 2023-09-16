@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.zwander.common.util.AdaptiveMod
import dev.zwander.common.util.PersistentMutableStateFlow
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
    renderItemTitle: @Composable ColumnScope.(T) -> Unit,
    renderItemDescription: @Composable ColumnScope.(T) -> Unit = {},
    renderItem: @Composable ColumnScope.(T) -> Unit,
    modifier: Modifier = Modifier,
    key: (T) -> Any = { it.hashCode() },
    gridContentPadding: PaddingValues? = null,
    bottomBarContents: (@Composable RowScope.() -> Unit)? = null,
    showBottomBarExpander: Boolean = true,
    id: String? = null,
    itemIsSelectable: @Composable T.() -> Boolean = { true },
) {
    LaunchedEffect(bottomBarContents, id, showBottomBarExpander) {
        if (bottomBarContents != null && id.isNullOrBlank() && showBottomBarExpander) {
            throw IllegalStateException("ID must be specified when bottom bar is provided and expander is shown.")
        }
    }

    var bottomBarHeight by remember {
        mutableStateOf(0)
    }

    val actualContentPadding = (gridContentPadding ?: PaddingValues(8.dp)).run {
        val layoutDirection = LocalLayoutDirection.current

        with (LocalDensity.current) {
            PaddingValues(
                start = calculateStartPadding(layoutDirection),
                top = calculateTopPadding(),
                end = calculateEndPadding(layoutDirection),
                bottom = calculateBottomPadding() + bottomBarHeight.toDp(),
            )
        }
    }

    val columns by remember(items.size) {
        derivedStateOf {
            AdaptiveMod(250.dp, items.size)
        }
    }

    Box(
        modifier = modifier,
    ) {
        LazyVerticalStaggeredGrid(
            contentPadding = actualContentPadding,
            columns = columns,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
            modifier = Modifier.matchParentSize(),
        ) {
            items(items = items, key = key) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                        .animateItemPlacement(),
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

                        CompositionLocalProvider(
                            LocalTextStyle provides MaterialTheme.typography.bodyMedium,
                        ) {
                            renderItemDescription(it)
                        }

                        Spacer(modifier = Modifier.size(8.dp))

                        if (itemIsSelectable(it)) {
                            SelectionContainer {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    renderItem(it)
                                }
                            }
                        } else {
                            renderItem(it)
                        }
                    }
                }
            }
        }

        bottomBarContents?.let { contents ->
            var showingBottomBar by remember {
                PersistentMutableStateFlow("${id}_bottom_bar_expanded", true)
            }.collectAsMutableState()

            Column(
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.75f))
                    .onSizeChanged { bottomBarHeight = it.height }
                    .padding(8.dp)
                    .align(Alignment.BottomCenter)
            ) {
                if (showBottomBarExpander) {
                    ExpanderCard(
                        expanded = showingBottomBar,
                        onExpandChange = { showingBottomBar = it },
                        inverted = true,
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.contentColorFor(MaterialTheme.colorScheme.background),
                        ),
                    )
                }

                AnimatedVisibility(showingBottomBar || !showBottomBarExpander) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (showBottomBarExpander) {
                            Spacer(modifier = Modifier.size(4.dp))
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            contents()
                        }
                    }
                }
            }
        }
    }
}
