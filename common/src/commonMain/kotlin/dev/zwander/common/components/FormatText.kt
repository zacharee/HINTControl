@file:OptIn(ExperimentalObjCRefinement::class)

package dev.zwander.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@Composable
@HiddenFromObjC
fun FormatText(
    text: String,
    textFormat: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = textFormat,
            textAlign = TextAlign.Center,
        )

        ProvideTextStyle(
            value = MaterialTheme.typography.bodySmall.copy(
                baselineShift = BaselineShift.Superscript,
            )
        ) {
            Text(
                text = text,
            )
        }
    }
}
