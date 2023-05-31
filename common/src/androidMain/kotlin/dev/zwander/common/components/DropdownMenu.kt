package dev.zwander.common.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MenuItemColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.window.PopupProperties

@Composable
actual fun DropdownMenuActual(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier,
    offset: DpOffset,
    scrollState: ScrollState,
    properties: dev.zwander.common.components.PopupProperties,
    content: @Composable ColumnScope.() -> Unit,
) {
    androidx.compose.material3.DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        offset = offset,
        // TODO: Not in the JB API yet.
//        scrollState = scrollState,
        properties = PopupProperties(
            focusable = properties.focusable,
            dismissOnBackPress = properties.dismissOnBackPress,
            dismissOnClickOutside = properties.dismissOnClickOutside,
            securePolicy = when (properties.securePolicy) {
                SecureFlagPolicy.Inherit -> androidx.compose.ui.window.SecureFlagPolicy.Inherit
                SecureFlagPolicy.SecureOn -> androidx.compose.ui.window.SecureFlagPolicy.SecureOn
                SecureFlagPolicy.SecureOff -> androidx.compose.ui.window.SecureFlagPolicy.SecureOff
            },
            excludeFromSystemGesture = properties.excludeFromSystemGesture,
            clippingEnabled = properties.clippingEnabled,
        ),
        content = content,
    )
}

@Composable
actual fun DropdownMenuItemActual(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    contentPadding: PaddingValues,
    interactionSource: MutableInteractionSource,
    colors: MenuItemColors,
    leadingIcon: (@Composable () -> Unit)?,
    trailingIcon: (@Composable () -> Unit)?,
) {
    androidx.compose.material3.DropdownMenuItem(
        text = text,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        colors = colors,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
    )
}
