import kotlin.math.abs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

fun assertEqualsNumerical(expected: Double, epsilon: Double, actual: Double?) {
    assertNotNull(actual) { "Expected: ${expected}, got: ${actual}" }
    assertTrue(abs(expected - actual) < epsilon, "Expected: ${expected}, got: ${actual}, numerically different!")
}