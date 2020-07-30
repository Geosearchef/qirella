package util

import kotlin.js.Date
import kotlin.math.pow
import kotlin.math.roundToInt

object Util {
    fun currentTimeMillis() = Date().getTime()
}

fun Double.toDecimals(n: Int) = (this * 10.0.pow(n)).roundToInt().toDouble() / 10.0.pow(n)