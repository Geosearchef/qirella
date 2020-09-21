package util.math

import util.math.Complex.Companion.j
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals


class ComplexTest {

    @Test
    fun testPlus() {
        assertEquals(4.0 + 1.0*j, (1.5 + 2.0*j) + (2.5 - 1.0*j))
    }

    @Test
    fun testMinus() {
        assertEquals(Complex(-1.0, 3.0), Complex(1.5, 2.0) - Complex(2.5, -1.0))
    }

    @Test
    fun testTimes() {
        assertEquals(Complex(5.75, 3.5), Complex(1.5, 2.0) * Complex(2.5, -1.0))
    }

    @Test
    fun testDiv() {
        assertEquals(Complex(0.4, 0.8), Complex(2.0, 2.0) / Complex(3.0, -1.0))
    }

    @Test
    fun testAbs() {
        assertEquals(5.0, Complex(3.0, 4.0).abs)
    }

    @Test
    fun testArg() {
        assertEquals(PI / 4.0, Complex(3.0, 3.0).arg)
    }

    @Test
    fun testConj() {
        assertEquals(Complex(3.0, -4.0), Complex(3.0, 4.0).conj())
    }
}