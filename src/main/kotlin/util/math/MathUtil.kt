package util.math

import kotlin.math.abs

fun Double.equalsNumerically(other: Double, threshold: Double = 0.01) = abs(this - other) < threshold

/**
 * implements the modulus operator, as kotlin only offers the remainder operator
 */
fun Double.mod(modulus: Double): Double {
    var res = this;
    while(res < 0.0) {
        res += modulus;
    }
    return res % modulus
}