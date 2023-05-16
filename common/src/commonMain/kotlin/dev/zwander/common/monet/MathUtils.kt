package dev.zwander.common.monet

import kotlin.math.pow

fun pow(b: Double, p: Double): Double {
    return b.pow(p)
}

fun lerp(start: Float, stop: Float, amount: Float): Float {
    return start + (stop - start) * amount
}
