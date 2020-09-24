package util.math

import util.math.Complex.Companion.j
import util.math.linalg.Matrix.Companion.I
import util.math.linalg.Matrix.Companion.PAULI_X
import util.math.linalg.complexArrayOf
import util.math.linalg.kroneckerOf
import util.math.linalg.matrixOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MatrixTest {

    @Test
    fun testPlus() {
        assertTrue((PAULI_X + I).m.all { it.all { it == 1.0 + 0.0*j } })
    }

    @Test
    fun testPlusMinusTimes() {
        assertEquals(PAULI_X + PAULI_X, PAULI_X * 3.0 - PAULI_X)
    }

    @Test
    fun testTranspose() {
        assertEquals(
            matrixOf(
                arrayOf(1.0 + 1.0*j, 2.0 + 2.0*j),
                arrayOf(3.0 + 3.0*j, 4.0 + 4.0*j)
            ),
            matrixOf(
                arrayOf(1.0 + 1.0*j, 3.0 + 3.0*j),
                arrayOf(2.0 + 2.0*j, 4.0 + 4.0*j)
            ).T
        )
        assertEquals(
            matrixOf(
                arrayOf(1.0 + 1.0*j, 2.0 + 2.0*j, 5.0 + 5.0*j),
                arrayOf(3.0 + 3.0*j, 4.0 + 4.0*j, 6.0 + 6.0*j)
            ),
            matrixOf(
                arrayOf(1.0 + 1.0*j, 3.0 + 3.0*j),
                arrayOf(2.0 + 2.0*j, 4.0 + 4.0*j),
                arrayOf(5.0 + 5.0*j, 6.0 + 6.0*j)
            ).T
        )
    }

    @Test
    fun testConjugateTranspose() {
        assertEquals(
            matrixOf(
                arrayOf(1.0 - 1.0*j, 2.0 - 2.0*j),
                arrayOf(3.0 - 3.0*j, 4.0 - 4.0*j)
            ),
            matrixOf(
                arrayOf(1.0 + 1.0*j, 3.0 + 3.0*j),
                arrayOf(2.0 + 2.0*j, 4.0 + 4.0*j)
            ).H
        )
    }

    @Test
    fun testMultiplication() {
        assertEquals(PAULI_X, I * PAULI_X)
        assertEquals(I, PAULI_X * PAULI_X)

        assertEquals(
            matrixOf(
                arrayOf(6.0 - 0.0*j, 12.0 - 0.0*j),
                arrayOf(0.0 + 0.0*j, 0.0 + 0.0*j)
            ),
            matrixOf(
                arrayOf(0.0 + 0.0*j, 3.0 + 0.0*j),
                arrayOf(0.0 + 0.0*j, 0.0 + 0.0*j)
            ) * matrixOf(
                arrayOf(1.0 + 1.0*j, 3.0 + 3000.0*j),
                arrayOf(2.0 + 0.0*j, 4.0 + 0.0*j)
            )
        )
    }

    @Test
    fun testKronecker() {
        assertEquals(
            matrixOf(
                arrayOf(0.0+0.0*j, 1.0+0.0*j, 0.0+0.0*j, 0.0+0.0*j),
                arrayOf(1.0+0.0*j, 0.0+0.0*j, 0.0+0.0*j, 0.0+0.0*j),
                arrayOf(0.0+0.0*j, 0.0+0.0*j, 0.0+0.0*j, 1.0+0.0*j),
                arrayOf(0.0+0.0*j, 0.0+0.0*j, 1.0+0.0*j, 0.0+0.0*j)
            ),
            I.kronecker(PAULI_X)
        )

        assertEquals(
            matrixOf(
                complexArrayOf(0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                complexArrayOf(1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                complexArrayOf(0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0),
                complexArrayOf(0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0),
                complexArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0),
                complexArrayOf(0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0),
                complexArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0),
                complexArrayOf(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0)
            ),
            kroneckerOf(I, I, PAULI_X)
        )
    }
}