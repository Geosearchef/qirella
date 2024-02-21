package qcn.simulation

import util.math.Complex.Companion.j
import util.math.linalg.Matrix
import util.math.linalg.Matrix.Companion.I
import util.math.linalg.kroneckerOf
import util.math.linalg.matrixOf
import util.math.linalg.times
import util.math.plus
import util.math.times
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

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

fun buildRotationMatrix(axis: Matrix, angle: Double): Matrix {
    val v = axis.asColumnVector()
    check(v.rows == 3 && v.abs == 1.0)

    // rotation around X/Y/Z is e^(-iaX/2) = cos(a/2)*I - i*sin(a/2)X, book page
    // rotation around v is e^(-ia(v*sigma)/2) = cos(a/2)*I - i * sin(a/2)(v*sigma)
    // with v*sigma = v_1*sig_1 + v_2*sig_2 + v_3*sig_3

    // project axis to combination of pauli matrices
    val vSigma = matrixOf(
        arrayOf(v.m[2][0], v.m[0][0] - j * v.m[1][0]),
        arrayOf(v.m[0][0] + j * v.m[1][0], -v.m[2][0])
    )

    val rotationMatrix = cos(angle / 2.0) * I - j * sin(angle / 2.0) * vSigma

    return rotationMatrix
}
