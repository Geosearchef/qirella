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
class MeasurementComponent(override var pos: Vector, val operators: List<Matrix>, val baseStates: List<ColumnVector>, val classicalTarget: Int) : CircuitComponent() {

    override val selectable = true

    // projective:
    constructor(pos: Vector = Vector(), baseStates: Array<ColumnVector>, classicalTarget: Int) : this(pos, baseStates.map { it * it.T }, baseStates.asList(), classicalTarget) // projective measurement operator is  |b> <b|
    constructor(pos: Vector = Vector(), classicalTarget: Int) : this(pos, arrayOf(Q0, Q1), classicalTarget)

    init {
        check(baseStates.all { it.isVector() }) { "Base states need to be vectors" }
    }

    // todo;: result after measurement

    // p = <phi| M_dag M |phi> = <phi| M | phi>    as M_dag = M and M^2 = M
    // abs(M |phi>)^2 = sqrt((M |phi>)_dag (M |phi>))^2 = (M |phi>)_dag (M |phi>) = <phi| M_dag M |phi> = p
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