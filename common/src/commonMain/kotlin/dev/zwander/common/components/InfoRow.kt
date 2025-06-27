@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.mvvm.flow.compose.collectAsMutableState
import dev.zwander.common.data.InfoMap
import dev.zwander.common.util.PersistentMutableStateFlow
import dev.zwander.common.util.animateContentHeight
import dev.zwander.common.util.animatePlacement
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalLayoutApi::class)
@Composable
@HiddenFromObjC
fun InfoRow(
    items: InfoMap,
    modifier: Modifier = Modifier,
    advancedItems: InfoMap? = null,
    expandedKey: String? = null,
) {
    val expandedState = if (expandedKey != null) {
        remember(expandedKey) {
            PersistentMutableStateFlow(expandedKey, false)
        }
    } else null

    @Suppress("IfThenToElvis")
    var expanded by if (expandedState != null) {
        expandedState.collectAsMutableState()
    } else {
        remember {
            mutableStateOf(false)
        }
    }

    Column(
        modifier = modifier,
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth().animateContentHeight(),
        ) {
            items.forEach { (_, info) ->
                info?.let {
                    info.Render(Modifier.padding(horizontal = 4.dp).animatePlacement())
                }
            }

            advancedItems?.forEach { (_, info) ->
                if (info != null) {
                    AnimatedVisibility(
                        visible = expanded,
                        enter = fadeIn() + expandVertically(expandFrom = Alignment.CenterVertically),
                        exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically),
                    ) {
                        info.Render(Modifier.padding(horizontal = 4.dp).animatePlacement())
                    }
                }
            }
        }

        advancedItems?.let {
            ExpanderCard(
                expanded = expanded,
                onExpandChange = { expanded = it },
            )
        }
    }
}
