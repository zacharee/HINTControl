@file:OptIn(ExperimentalObjCRefinement::class)
@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "EXPOSED_PARAMETER_TYPE")

package dev.zwander.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.LocalShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import dev.zwander.common.util.animateContentWidth
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun <T> LabeledDropdown(
    label: String,
    expanded: Boolean,
    selectedValue: T,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    offset: DpOffset = DpOffset.Zero,
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

        Box {
            AnimatedCard(
                onClick = {
                    onExpandChange(!expanded)
                },
                enabled = !expanded,
                colors = CardDefaults.outlinedCardColors(),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .heightIn(min = 36.dp)
                        .widthIn(min = 64.dp)
                        .padding(8.dp)
                        .animateContentWidth(),
                ) {
                    Text(
                        text = valueToString(selectedValue),
                    )
                }
            }

            CompositionLocalProvider(
                LocalShapes provides LocalShapes.current.copy(
                    extraSmall = LocalShapes.current.medium,
                ),
            ) {
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { onExpandChange(false) },
                    content = content,
                    offset = offset,
                    properties = properties,
                )
            }
        }
    }
}

@Composable
@HiddenFromObjC
fun SelectableDropdownMenuItem(
    text: @Composable () -> Unit,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    colors: MenuItemColors = MenuDefaults.itemColors(),
    contentPadding: PaddingValues = MenuDefaults.DropdownMenuItemContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    DropdownMenuItem(
        text = text,
        onClick = onClick,
        leadingIcon = leadingIcon,
        modifier = modifier.then(
            if (isSelected) {
                Modifier.background(MaterialTheme.colorScheme.secondaryContainer)
            } else {
                Modifier
            }
        ),
        trailingIcon = trailingIcon,
        enabled = enabled,
        colors = MenuDefaults.itemColors(
            textColor = if (isSelected) {
                MaterialTheme.colorScheme.onSecondaryContainer
            } else {
                colors.textColor(true)
            },
            leadingIconColor = colors.leadingIconColor(true),
            trailingIconColor = colors.trailingIconColor(true),
            disabledTextColor = colors.textColor(false),
            disabledLeadingIconColor = colors.leadingIconColor(false),
            disabledTrailingIconColor = colors.trailingIconColor(false),
        ),
        contentPadding = contentPadding,
        interactionSource = interactionSource,
    )
}
