package qcn.simulation

import qcn.circuit.Circuit
import qcn.circuit.GateComponent
import qcn.circuit.MeasurementComponent
import util.math.Complex.Companion.j
import util.math.linalg.ColumnVector
import util.math.linalg.Matrix
import util.math.plus
import util.math.randomWeightedIndex
import util.math.times
import kotlin.math.pow
import kotlin.random.Random

object StatevectorSimulator {

    fun singleShotRun(circuit: Circuit): ExecutionState {
        val executionPath = buildExecutionPath(circuit)
        val state = executePath(executionPath)
        return state
    }
    fun multiShotRun(circuit: Circuit, shots: Int): Map<String, Double> {
        println("Running simulator, $shots shots")

        val states = (0 until shots).map { singleShotRun(circuit) }
        val measuredResults = HashMap<String, Double>()
        states.map { it.classicalRegisters }.map { it.joinToString("") }.forEach {
            measuredResults[it] = measuredResults.getOrPut(it) { 0.0 } + 1.0
        }
        measuredResults.keys.forEach {
            measuredResults[it] = measuredResults[it]!! / shots.toDouble()
        }
        return measuredResults
    }


    fun buildExecutionPath(circuit: Circuit): ExecutionPath {
        val start = circuit.components.minOf { it.pos.x }.toInt()
        val end = circuit.components.maxOf { it.pos.x }.toInt() // inclusive
        val qubits = circuit.getUsedRegisters()
        val classicalRegisters = circuit.getClassicalRegisters()

        val steps = ArrayList<ExecutionStep>()

        for(t in start .. end) {
            // gates
            val gatesOnLayer = circuit.components
                .filterIsInstance<GateComponent>()
                .filter { it.timestep == t }
                .filter { it.controlComponents.isEmpty() }
            if(gatesOnLayer.isNotEmpty()) {
                val gatesByQubitIndex = gatesOnLayer.map { it.qubitIndex to it.matrix }.toMap()
                val gateApplicationMatrix = multiQubitMultiGate(gatesByQubitIndex, qubits)
                steps.add(MatrixMultiplicationStep(gateApplicationMatrix))
            }

            // controlled gates
            val controlledGatesOnLayer = circuit.components
                .filterIsInstance<GateComponent>()
                .filter { it.timestep == t }
                .filter { it.controlComponents.isNotEmpty() }
            controlledGatesOnLayer.forEach { gate ->
                steps.add(MatrixMultiplicationStep(
                    multiQubitSingleControlledGate(
                        gate.matrix,
                        qubits.indexOf(gate.qubitIndex),
                        gate.controlComponents.map { qubits.indexOf(it.qubitIndex) },
                        qubits.size
                    )
                ))
            }

            // multi-qubit
            // TODO: not yet supported

            // measurement
            val measurementComponents = circuit.components
                .filterIsInstance<MeasurementComponent>()
                .filter { it.timestep == t }
            measurementComponents.forEach { comp ->
                steps.add(MeasurementStep(comp.operators.map { multiQubitSingleGate(it, qubits.indexOf(comp.qubitIndex), qubits.size) }, classicalRegisters.indexOf(comp.classicalTarget)))
            }
        }

        return ExecutionPath(steps, qubits.size, classicalRegisters.size)
    }

    class ExecutionPath(val steps: List<ExecutionStep>, val qubits: Int, val classicalBits: Int)
    abstract class ExecutionStep {}
    class MatrixMultiplicationStep(val matrix: Matrix) : ExecutionStep() {}
    class MeasurementStep(val operators: List<Matrix>, val classicalTarget: Int) : ExecutionStep() {} // TODO: this just outputs the operator index


    fun executePath(path: ExecutionPath, random: Random = Random.Default): ExecutionState {
        val state = ExecutionState(path.qubits, path.classicalBits)

        for(step in path.steps) {
            when(step) {
                is MatrixMultiplicationStep -> {
                    state.stateVector = (step.matrix * state.stateVector).asColumnVector()
                }
                is MeasurementStep -> {
                    val probabilities = step.operators.map { op -> (op * state.stateVector).asColumnVector().abs.pow(2.0) }  // formula 2.61 p(m) = || M_m |psi> ||^2
                    val selectedIndex = randomWeightedIndex(probabilities, random)

                    state.classicalRegisters[step.classicalTarget] = selectedIndex
                    state.stateVector = (step.operators[selectedIndex] * state.stateVector).asColumnVector()
                    state.stateVector = (state.stateVector / state.stateVector.abs).asColumnVector()
                }
            }
        }

        return state
    }

    class ExecutionState(val qubits: Int, val classicalBits: Int) {
        var stateVector = ColumnVector(Array(2.0.pow(qubits).toInt()) { 0.0 + 0.0*j }).apply {
            m[0][0] = 1.0 + 0.0*j
        }
        var classicalRegisters = Array(classicalBits) { -1 }
    }
}