package dev.zwander.common.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun <T : Number> SliderWithTitle(
    title: String,
    minValue: T,
    maxValue: T,
    currentValue: T,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    unit: String = ""
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = title,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "$currentValue$unit"
            )
        }

        Slider(
            value = currentValue.toFloat(),
            valueRange = minValue.toFloat() .. maxValue.toFloat(),
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
