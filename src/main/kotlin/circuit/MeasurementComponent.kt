package circuit

import util.math.Vector
import util.math.linalg.Matrix
import util.math.linalg.Matrix.Companion.Q0
import util.math.linalg.Matrix.Companion.Q1

class MeasurementComponent(pos: Vector, val operators: List<Matrix>, val baseStates: List<Matrix>) : CircuitComponent(pos) {

    // projective:
    constructor(pos: Vector, baseStates: Array<Matrix>) : this(pos, baseStates.map { it * it.T }, baseStates.asList()) // projective measurement operator is  |b> <b|
    constructor(pos: Vector) : this(pos, arrayOf(Q0, Q1))

    init {
        check(baseStates.all { it.isVector() }) { "Base states need to be vectors" }
    }

    fun resultProbability(operator: Matrix, state: Matrix, measuredQubitIndex: Int): Double {
        require(state.isVector())

        return 0.0//TODO
    }
}