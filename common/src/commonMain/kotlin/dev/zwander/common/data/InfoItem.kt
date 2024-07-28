package dev.zwander.common.data

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource
import dev.zwander.common.components.FormatText

typealias InfoMap = Map<StringResource, InfoItem<*>?>
typealias MutableInfoMap = MutableMap<StringResource, InfoItem<*>?>

@Composable
fun generateInfoList(vararg dataKeys: Any?, block: MutableInfoMap.() -> Unit): InfoMap {
    val mapState = rememberSaveable(dataKeys) {
        LinkedHashMap<StringResource, InfoItem<*>?>().also(block)
    }

    return mapState
}

inline operator fun MutableInfoMap.set(key: StringResource, value: String?) {
    this[key] = value?.let { InfoItem.StringItem(key, it) }
}

inline operator fun MutableInfoMap.set(key: StringResource, value: StringResource?) {
    this[key] = value?.let { InfoItem.StringResourceItem(key, it) }
}

// Triple: <value, min, max>
inline operator fun MutableInfoMap.set(key: StringResource, value: Triple<Int?, Int, Int>) {
    this[key] = value.first?.let { InfoItem.ColorGradientItem(key, it, value.second, value.third) }
}

sealed interface InfoItem<T: Any?> {
    val label: StringResource
    val value: T

    @Composable
    fun Render(modifier: Modifier)

    data class StringItem(
        override val label: StringResource,
        override val value: String,
    ) : InfoItem<String> {
        @Composable
        override fun Render(modifier: Modifier) {
            FormatText(
                text = stringResource(label),
                value = value,
                modifier = modifier,
            )
        }
    }

    data class StringResourceItem(
        override val label: StringResource,
        override val value: StringResource,
    ) : InfoItem<StringResource> {
        @Composable
        override fun Render(modifier: Modifier) {
            FormatText(
                text = stringResource(label),
                value = stringResource(value),
                modifier = modifier,
            )
        }
    }

    data class ColorGradientItem(
        override val label: StringResource,
        override val value: Int,
        val minValue: Int,
        val maxValue: Int,
    ) : InfoItem<Int> {
        // https://www.astrouxds.com/patterns/status-system/
        companion object {
            private val darkModeRed = Color(0xFFFF3838)
            private val darkModeGreen = Color(0xFF56F000)

            private val lightModeRed = Color(0xFFFF2A04)
            private val lightModeGreen = Color(0xFF00E200)
        }

        @Composable
        override fun Render(modifier: Modifier) {
            val shouldUseDarkColor = LocalContentColor.current.luminance() > 0.5f

            val scaledFraction = ((value - minValue).toFloat() / (maxValue - minValue).toFloat())
                .coerceAtLeast(0f)
                .coerceAtMost(1f)

            val color = lerp(
                start = if (shouldUseDarkColor) darkModeRed else lightModeRed,
                stop = if (shouldUseDarkColor) darkModeGreen else lightModeGreen,
                fraction = scaledFraction,
            )
            val colorState by animateColorAsState(color)

            FormatText(
                text = stringResource(label),
                value = value.toString(),
                modifier = modifier,
                valueColor = colorState,
            )
        }
    }
}
