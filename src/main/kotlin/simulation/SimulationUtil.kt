package simulation

import util.math.linalg.Matrix
import util.math.linalg.Matrix.Companion.I
import util.math.linalg.kroneckerOf

fun multiQubitSingleGate(gate: Matrix, qubitIndex: Int, qubitCount: Int): Matrix {
    val gates = (0 until qubitCount).map { if(it == qubitIndex) gate else I }.toTypedArray()
    return kroneckerOf(*gates)
}