@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import dev.zwander.common.util.animateContentWidth
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@HiddenFromObjC
fun <T> LabeledDropdown(
    label: String,
    expanded: Boolean,
    selectedValue: T,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Zero,
    scrollState: ScrollState = rememberScrollState(),
    properties: PopupProperties = PopupProperties(focusable = true),
    valueToString: @Composable (T) -> String = { it.toString() },
    content: @Composable ColumnScope.() -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f).padding(start = 8.dp),
        )

        Card(
            onClick = {
                onExpandChange(!expanded)
            },
            enabled = !expanded,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .padding(8.dp)
                    .animateContentWidth(),
            ) {
                Text(
                    text = valueToString(selectedValue),
                )

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onExpandChange(false) },
                    modifier = Modifier.align(Alignment.CenterEnd),
                    content = content,
                    offset = offset,
                    properties = properties,
                    scrollState = scrollState,
                )
            }
        }
    }
}

@Composable
@HiddenFromObjC
fun DropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Zero,
    scrollState: ScrollState = rememberScrollState(),
    properties: PopupProperties = PopupProperties(focusable = true),
    content: @Composable ColumnScope.() -> Unit,
) {
    DropdownMenuActual(
        expanded, onDismissRequest, modifier,
        offset, scrollState, properties, content
    )
}

@Composable
@HiddenFromObjC
expect fun DropdownMenuActual(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    offset: DpOffset,
    scrollState: ScrollState,
    properties: PopupProperties,
    content: @Composable ColumnScope.() -> Unit,
)

@Composable
@HiddenFromObjC
fun DropdownMenuItem(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: MenuItemColors = MenuDefaults.itemColors(),
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
) {
    DropdownMenuItemActual(
        text, onClick, modifier, enabled,
        contentPadding, interactionSource, colors,
        leadingIcon, trailingIcon
    )
}

@Composable
@HiddenFromObjC
expect fun DropdownMenuItemActual(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    contentPadding: PaddingValues,
    interactionSource: MutableInteractionSource,
    colors: MenuItemColors,
    leadingIcon: (@Composable () -> Unit)?,
    trailingIcon: (@Composable () -> Unit)?,
)

data class PopupProperties(
    val focusable: Boolean = false,
    val dismissOnBackPress: Boolean = true,
    val dismissOnClickOutside: Boolean = true,
    val securePolicy: SecureFlagPolicy = SecureFlagPolicy.Inherit,
    val excludeFromSystemGesture: Boolean = true,
    val clippingEnabled: Boolean = true,
    val usePlatformDefaultWidth: Boolean = false
)

enum class SecureFlagPolicy {
    Inherit,
    SecureOn,
    SecureOff
}
