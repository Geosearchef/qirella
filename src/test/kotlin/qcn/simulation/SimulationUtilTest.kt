package qcn.simulation

import util.math.linalg.Matrix
import util.math.linalg.complexArrayOf
import util.math.linalg.matrixOf
import kotlin.test.Test
import kotlin.test.assertEquals

class SimulationUtilTest {

    @Test
    fun testControlledGate() {
        val mat = multiQubitSingleControlledGate(Matrix.PAULI_X, 1, listOf(0), 2)
        assertEquals(matrixOf(
            complexArrayOf(1.0, 0.0, 0.0, 0.0),
            complexArrayOf(0.0, 1.0, 0.0, 0.0),
            complexArrayOf(0.0, 0.0, 0.0, 1.0),
            complexArrayOf(0.0, 0.0, 1.0, 0.0)
        ), mat)
    }

}