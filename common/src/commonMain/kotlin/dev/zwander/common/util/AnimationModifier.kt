package dev.zwander.common.util

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationEndReason
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.IntrinsicMeasurable
import androidx.compose.ui.layout.IntrinsicMeasureScope
import androidx.compose.ui.layout.LayoutModifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun Modifier.animateContentWidth(
    animationSpec: FiniteAnimationSpec<Int> = spring(),
    finishedListener: ((initialValue: Int, targetValue: Int) -> Unit)? = null,
) = animateContentSize(Which.WIDTH, animationSpec, finishedListener)

fun Modifier.animateContentHeight(
    animationSpec: FiniteAnimationSpec<Int> = spring(),
    finishedListener: ((initialValue: Int, targetValue: Int) -> Unit)? = null,
) = animateContentSize(Which.HEIGHT, animationSpec, finishedListener)

fun Modifier.animateContentSize(
    which: Which,
    animationSpec: FiniteAnimationSpec<Int> = spring(),
    finishedListener: ((initialValue: Int, targetValue: Int) -> Unit)? = null,
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "animateContentSize"
        properties["animationSpec"] = animationSpec
        properties["finishedListener"] = finishedListener
    }
) {
    val scope = rememberCoroutineScope()
    val animModifier = remember(scope) {
        SizeAnimationModifier(animationSpec, scope, which)
    }
    animModifier.listener = finishedListener
    this.clipToBounds().then(animModifier)
}

enum class Which {
    WIDTH,
    HEIGHT,
}

/**
 * This class creates a [LayoutModifier] that measures children, and responds to children's size
 * change by animating to that size. The size reported to parents will be the animated size.
 */
private class SizeAnimationModifier(
    val animSpec: AnimationSpec<Int>,
    val scope: CoroutineScope,
    val which: Which,
) : LayoutModifierWithPassThroughIntrinsics() {
    var listener: ((startSize: Int, endSize: Int) -> Unit)? = null

    data class AnimData(
        val anim: Animatable<Int, AnimationVector1D>,
        var startSize: Int
    )

    var animData: AnimData? by mutableStateOf(null)

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val placeable = measurable.measure(constraints)

        val measuredSize = IntSize(placeable.width, placeable.height)
        val result = animateTo(if (which == Which.WIDTH) measuredSize.width else measuredSize.height)

        val (width, height) = (if (which == Which.WIDTH) result else measuredSize.width) to (if (which == Which.WIDTH) measuredSize.height else result)
        return layout(width, height) {
            placeable.placeRelative(0, 0)
        }
    }

    fun animateTo(targetSize: Int): Int {
        val data = animData?.apply {
            if (targetSize != anim.targetValue) {
                startSize = anim.value
                scope.launch {
                    val result = anim.animateTo(targetSize, animSpec)
                    if (result.endReason == AnimationEndReason.Finished) {
                        listener?.invoke(startSize, result.endState.value)
                    }
                }
            }
        } ?: AnimData(
            Animatable(
                targetSize, Int.VectorConverter, 1
            ),
            targetSize
        )

        animData = data
        return data.anim.value
    }
}

abstract class LayoutModifierWithPassThroughIntrinsics : LayoutModifier {
    final override fun IntrinsicMeasureScope.minIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.minIntrinsicWidth(height)

    final override fun IntrinsicMeasureScope.minIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.minIntrinsicHeight(width)

    final override fun IntrinsicMeasureScope.maxIntrinsicWidth(
        measurable: IntrinsicMeasurable,
        height: Int
    ) = measurable.maxIntrinsicWidth(height)

    final override fun IntrinsicMeasureScope.maxIntrinsicHeight(
        measurable: IntrinsicMeasurable,
        width: Int
    ) = measurable.maxIntrinsicHeight(width)
}
