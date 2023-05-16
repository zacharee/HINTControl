package dev.zwander.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
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
            value = TextStyle(
                baselineShift = BaselineShift.Superscript
            )
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
            )
        }
    }
}
