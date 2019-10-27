package fe.util.gui.util

import kotlin.math.roundToInt

/**
 * Linearly interpolate between [start] and [stop] with [fraction] fraction between them.
 */
fun lerp(start: Float, stop: Float, fraction: Float): Float {
    return start + (stop - start) * fraction
}

/**
 * Linearly interpolate between [start] and [stop] with [fraction] fraction between them.
 */
fun lerp(start: Int, stop: Int, fraction: Float): Int {
    return start + ((stop - start) * fraction).roundToInt()
}

fun Float.toStringAsFixed(digits: Int) = String.format("%.${digits}f", this)