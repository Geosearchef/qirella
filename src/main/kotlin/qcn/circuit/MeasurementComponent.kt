package qcn.circuit

import kotlinx.serialization.Serializable
import qcn.simulation.multiQubitSingleGate
import util.math.Vector
import util.math.linalg.ColumnVector
import util.math.linalg.Matrix
import util.math.linalg.Matrix.Companion.Q0
import util.math.linalg.Matrix.Companion.Q1
import kotlin.math.pow

@Serializable
class MeasurementComponent(override var pos: Vector, val operators: List<Matrix>, val baseStates: List<ColumnVector>) : CircuitComponent() {

    override val selectable = true

    // projective:
    constructor(pos: Vector = Vector(), baseStates: Array<ColumnVector>) : this(pos, baseStates.map { it * it.T }, baseStates.asList()) // projective measurement operator is  |b> <b|
    constructor(pos: Vector = Vector()) : this(pos, arrayOf(Q0, Q1))

    init {
        check(baseStates.all { it.isVector() }) { "Base states need to be vectors" }
    }

    fun resultProbability(operator: Matrix, state: ColumnVector, measuredQubitIndex: Int = 0): Double {
        val multiGateOperator = multiQubitSingleGate(operator, qubitIndex = measuredQubitIndex, qubitCount = state.qubits)
        val prob = (multiGateOperator * state).asColumnVector().abs.pow(2.0)
        return prob
    }
    fun resultProbability(baseState: ColumnVector, state: ColumnVector, measuredQubitIndex: Int = 0)
            = operatorForBaseState(baseState)?.let { resultProbability(it, state, measuredQubitIndex) }

    fun operatorForBaseState(baseState: ColumnVector): Matrix? {
        val i = baseStates.indexOf(baseState)
        return if(i != -1) operators[i] else null
    }
}