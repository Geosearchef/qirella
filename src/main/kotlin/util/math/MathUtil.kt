package util.math

import kotlin.math.abs
import kotlin.random.Random

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

fun randomWeightedIndex(weights: List<Double>, random: Random = Random.Default): Int {
    val sum = weights.sum()
    var r = random.nextDouble(sum)
    for(i in weights.indices) {
        r -= weights[i]
        if(r < 0.0) {
            return i
        }
    }
    return weights.lastIndex
}