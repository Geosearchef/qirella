package qcn.circuit

import util.math.Complex
import util.math.Complex.Companion.j
import util.math.Vector
import util.math.linalg.Matrix
import util.math.linalg.matrixOf
import util.math.plus
import util.math.times
import kotlin.math.PI
import kotlin.math.sqrt

enum class GateGenerator(val generate: (Vector) -> GateComponent, val placeable: Boolean = true) {
    X({ GateComponent("X", Matrix.PAULI_X, "#278f42", pos = it) }),
    Y({ GateComponent("Y", Matrix.PAULI_Y, "#278f42", pos = it) }),
    Z({ GateComponent("Z", Matrix.PAULI_Z, "#278f42", pos = it) }),
    HADAMARD({ GateComponent("H", matrixOf(arrayOf(1.0+0.0*j, 1.0+0.0*j), arrayOf(1.0+0.0*j, -1.0+0.0*j)) / sqrt(2.0), "#f2e44d", pos = it) }),
    S({ GateComponent("S", matrixOf(arrayOf(1.0+0.0*j, 0.0+0.0*j), arrayOf(0.0+0.0*j, 0.0+1.0*j)), "#278f42", pos = it) }),
    T({ GateComponent("T", matrixOf(arrayOf(1.0+0.0*j, 0.0+0.0*j), arrayOf(0.0+0.0*j, Complex.exp(PI / 4.0))), "#278f42", pos = it) }),
    ID({ GateComponent("ID", Matrix.I, "#278f42", pos = it) });

    val dummy = generate(Vector())
}