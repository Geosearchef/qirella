package circuit

import util.math.Complex.Companion.j
import util.math.linalg.Matrix
import util.math.linalg.matrixOf
import util.math.plus
import util.math.times
import kotlin.math.sqrt

enum class GateType(val representation: String, val matrix : Matrix) {
    X("X", Matrix.PAULI_X),
    Y("Y", Matrix.PAULI_Y),
    Z("Z", Matrix.PAULI_Z),
    HADAMARD("H", matrixOf(arrayOf(1.0+0.0*j, 1.0+0.0*j), arrayOf(1.0+0.0*j, -1.0+0.0*j)) / sqrt(2.0)),
    CUSTOM("", matrixOf(arrayOf())) {
        override fun apply(stateVector: Any?): Any? {
            return super.apply(stateVector)
        }
    };

    open fun apply(stateVector : Any?) : Any? {
        //TODO

        return null
    }
}