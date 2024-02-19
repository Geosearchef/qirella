package qcn.circuit

import assertEqualsNumerical
import util.math.Vector
import util.math.linalg.Matrix.Companion.Q0
import util.math.linalg.Matrix.Companion.Q1
import util.math.linalg.Matrix.Companion.Qminus
import util.math.linalg.Matrix.Companion.Qplus
import kotlin.test.Test
import kotlin.test.assertEquals

class MeasurementComponentTest {

    val computationalMeasurement = MeasurementComponent(Vector(), baseStates = arrayOf(Q0, Q1), classicalTarget = 0)
    val plusMinusMeasurement = MeasurementComponent(Vector(), baseStates = arrayOf(Qplus, Qminus), classicalTarget = 0)


    @Test
    fun testResultProbability() {
        assertEquals(1.0, computationalMeasurement.resultProbability(baseState = Q0, state = Q0))
        assertEquals(0.0, computationalMeasurement.resultProbability(baseState = Q0, state = Q1))

        assertEquals(1.0, computationalMeasurement.resultProbability(baseState = Q1, state = Q1))
        assertEquals(0.0, computationalMeasurement.resultProbability(baseState = Q1, state = Q0))

        assertEqualsNumerical(0.5, 1e-8, computationalMeasurement.resultProbability(baseState = Q0, state = Qplus))
        assertEqualsNumerical(0.5, 1e-8, computationalMeasurement.resultProbability(baseState = Q1, state = Qplus))
        assertEqualsNumerical(0.5, 1e-8, computationalMeasurement.resultProbability(baseState = Q0, state = Qminus))
        assertEqualsNumerical(0.5, 1e-8, computationalMeasurement.resultProbability(baseState = Q1, state = Qminus))

        assertEqualsNumerical(1.0, 1e-8, plusMinusMeasurement.resultProbability(baseState = Qplus, state = Qplus))
        assertEqualsNumerical(0.0, 1e-8, plusMinusMeasurement.resultProbability(baseState = Qplus, state = Qminus))
        assertEqualsNumerical(0.5, 1e-8, plusMinusMeasurement.resultProbability(baseState = Qplus, state = Q0))
        assertEqualsNumerical(0.5, 1e-8, plusMinusMeasurement.resultProbability(baseState = Qminus, state = Q1))
    }
}