package util

import kotlin.js.Date
import kotlin.math.pow
import kotlin.math.roundToInt

object Util {
    fun currentTimeMillis() = Date().getTime()
}

fun Double.toDecimals(n: Int): String {
    val stringified = this.toDecimalsAsDouble(n).toString()
    val digitsAfterSeparator = stringified.split('.').getOrNull(1)?.length ?: 0
    return stringified + (if(digitsAfterSeparator == 0) "." else "") + "0".repeat(n - digitsAfterSeparator)
}

fun Double.toDecimalsAsDouble(n: Int) = (this * 10.0.pow(n)).roundToInt().toDouble() / 10.0.pow(n)