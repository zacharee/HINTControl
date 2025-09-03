package dev.zwander.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp

enum class TransmissionPower(val raw: String) {
    FULL("100%"),
    HALF("50%"),
    ;

    companion object {
        fun fromRaw(raw: String, default: TransmissionPower = FULL): TransmissionPower {
            return entries.firstOrNull { it.raw == raw } ?: default
        }
    }
}

@Composable
fun TransmissionPowerGroup(
    currentPower: TransmissionPower,
    onPowerChange: (TransmissionPower) -> Unit,
    title: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = title,
            modifier = Modifier.padding(start = 8.dp),
        )

        Column(
            modifier = Modifier.selectableGroup(),
        ) {
            TransmissionPower.entries.forEach { powerOption ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .clip(MaterialTheme.shapes.small)
                        .heightIn(min = 48.dp)
                        .selectable(
                            selected = currentPower == powerOption,
                            onClick = { onPowerChange(powerOption) },
                            role = Role.RadioButton,
                        )
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = currentPower == powerOption,
                        onClick = null,
                    )

                    Text(
                        text = powerOption.raw,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp),
                    )
                }
            }
        }
    }
}
