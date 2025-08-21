@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.selection.DisableSelection
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.compose.alertdialog.InWindowAlertDialog
import dev.zwander.resources.common.MR
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun FormatText(
    text: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Unspecified,
    detailsText: String? = null,
) {
    Box(
        contentAlignment = Alignment.Center,
    ) {
        val density = LocalDensity.current

        var showingDetailsDialog by remember {
            mutableStateOf(false)
        }

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                textAlign = TextAlign.Center,
                lineHeight = LocalTextStyle.current.fontSize,
                color = valueColor,
            )

            ProvideTextStyle(
                value = MaterialTheme.typography.bodySmall,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    val fontSize = LocalTextStyle.current.fontSize

                    Text(
                        text = text,
                        lineHeight = fontSize,
                    )

                    val fontSizeInDp = remember(fontSize, density) {
                        with (density) {
                            fontSize.toDp()
                        }
                    }

                    if (detailsText != null) {
                        CompositionLocalProvider(
                            LocalMinimumInteractiveComponentSize provides 0.dp,
                        ) {
                            Box(
                                modifier = Modifier
                                    .sizeIn(maxWidth = fontSizeInDp, maxHeight = fontSizeInDp)
                                    .clip(CircleShape)
                                    .clickable(
                                        onClick = {
                                            showingDetailsDialog = true
                                        },
                                        role = Role.Button,
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = ripple(bounded = false),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = stringResource(MR.strings.more_info),
                                )
                            }
                        }
                    }
                }
            }
        }

        if (detailsText != null) {
            DisableSelection {
                InWindowAlertDialog(
                    showing = showingDetailsDialog,
                    onDismissRequest = { showingDetailsDialog = false },
                    title = { Text(text = text) },
                    text = {
                        SelectionContainer {
                            Markdown(content = detailsText)
                        }
                    },
                    buttons = {
                        TextButton(
                            onClick = {
                                showingDetailsDialog = false
                            },
                        ) {
                            Text(text = stringResource(MR.strings.ok))
                        }
                    },
                )
            }
        }
    }
}
