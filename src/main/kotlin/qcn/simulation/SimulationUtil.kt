package qcn.simulation

import util.math.linalg.Matrix
import util.math.linalg.Matrix.Companion.I
import util.math.linalg.kroneckerOf

fun multiQubitSingleGate(gate: Matrix, qubitIndex: Int, qubitCount: Int): Matrix {
    val gates = (0 until qubitCount).map { if(it == qubitIndex) gate else I }.toTypedArray()
    return kroneckerOf(*gates)
}

fun multiQubitMultiGate(gatesByQubitIndex: Map<Int, Matrix>, qubitIndices: List<Int>): Matrix {
    val gates = qubitIndices.map { gatesByQubitIndex[it] ?: I }.toTypedArray()
    return kroneckerOf(*gates)
}

fun multiQubitSingleControlledGate(gate: Matrix, qubitIndex: Int, controlQubits: List<Int>, qubitCount: Int): Matrix {
    return Matrix() // TODO
}
