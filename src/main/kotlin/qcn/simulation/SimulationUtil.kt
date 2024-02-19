package qcn.simulation

import util.math.Complex.Companion.j
import util.math.linalg.Matrix
import util.math.linalg.Matrix.Companion.I
import util.math.linalg.kroneckerOf
import util.math.plus
import util.math.times
import kotlin.math.pow

fun multiQubitSingleGate(gate: Matrix, qubitIndex: Int, qubitCount: Int): Matrix {
    val gates = (0 until qubitCount).map { if(it == qubitIndex) gate else I }.toTypedArray()
    return kroneckerOf(*gates)
}

fun multiQubitMultiGate(gatesByQubitIndex: Map<Int, Matrix>, qubitIndices: List<Int>): Matrix {
    val gates = qubitIndices.map { gatesByQubitIndex[it] ?: I }.toTypedArray()
    return kroneckerOf(*gates)
}

fun multiQubitSingleControlledGate(gate: Matrix, qubitIndex: Int, controlQubits: List<Int>, qubitCount: Int): Matrix {
    val matrix = Matrix(2.0.pow(qubitCount).toInt(), 2.0.pow(qubitCount).toInt())

    val controlMask = controlQubits.sumOf { (0b1).shl(qubitCount - 1 - it) }
    val targetMask = (0b1).shl(qubitCount - 1 - qubitIndex)

    // consider iterating over input states and write specific output state
    for(inputState in matrix.m[0].indices) {
        val controlBits = controlMask.and(inputState)
        if(controlBits != controlMask) { // at least one control bit is not set
            matrix.m[inputState][inputState] = 1.0 + 0.0*j  // copy input to same output
            continue
        }

        // copy input to output given column of gate matrix based on input and output based on gate matrix row
        val targetInput = targetMask.and(inputState)
        matrix.m[inputState.and(targetMask.inv())][inputState] = gate.m[0][if(targetInput == 0) 0 else 1]
        matrix.m[inputState.or(targetMask)][inputState] = gate.m[1][if(targetInput == 0) 0 else 1]
    }

    return matrix
}
