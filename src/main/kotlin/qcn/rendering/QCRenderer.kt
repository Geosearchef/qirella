package qcn.rendering

import qcn.QCComposer
import qcn.QCComposer.GATE_SIZE
import qcn.QCComposer.GATE_SIZE_WORLD_SPACE
import qcn.QCComposer.GRID_SIZE
import qcn.circuit.*
import rendering.*
import rendering.Rendering.ctx
import scene.Scene
import util.math.Vector
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object QCRenderer : Scene.SceneRenderer {

    const val GATE_COLOR = "#278f42"
    const val CIRCUIT_COLOR = "#4a4a4a"

    const val MEASUREMENT_SYMBOL_RADIUS = 15.0

    const val SELECTION_COLOR = "#27408f"
    const val SELECTION_INDICATOR_WIDTH = 5.0

    const val ACTION_BUTTON_COLOR = "#dddddd"

    override fun render() {
        renderCircuit()
    }

    private fun renderCircuit() {
        ctx.scale(QCComposer.scale, QCComposer.scale)
        ctx.translate(QCComposer.offset.x * GRID_SIZE, QCComposer.offset.y * GRID_SIZE)

        renderRegisters()

        renderComponents()

        ctx.setIdentityMatrix()
    }

    private fun renderRegisters() {
        val elements = QCComposer.circuit.components
        if (elements.isEmpty()) {
            return
        }

        val firstGate: Double = elements.map { it.pos.x }.minOrNull()!!
        val lastGate: Double = elements.map { it.pos.x }.maxOrNull()!!

        // Draw horizontal lines
        ctx.color(CIRCUIT_COLOR)
        elements.map { it.pos.y }.distinct().sorted().forEach {
            ctx.drawLine(
                toRenderSpace(
                    Vector(
                        firstGate - GATE_SIZE_WORLD_SPACE,
                        it + GATE_SIZE_WORLD_SPACE / 2
                    )
                ),
                toRenderSpace(
                    Vector(
                        lastGate + 2 * GATE_SIZE_WORLD_SPACE,
                        it + GATE_SIZE_WORLD_SPACE / 2
                    )
                )
            )
        }

        ctx.font = "15px sans-serif"
        QCComposer.circuit.getUsedRegisters().forEach { register ->
            //TODO: extract rendering procedure
            val registerPos = (toRenderSpace(Vector(firstGate - GATE_SIZE_WORLD_SPACE, register.toDouble() + GATE_SIZE_WORLD_SPACE / 2.0))
                    + Vector(-35.0, 0.0))
            ctx.fillTextCentered("|", registerPos + Vector(y = -1.0))
            ctx.fillTextCentered("0⟩", registerPos + Vector(x = 11.0))
        }
    }

    private fun renderComponents() {
        val renderedComponents = ArrayList<CircuitComponent>(QCComposer.circuit.components)
        renderedComponents.addAll(QCComposer.circuit.components)
        QCComposer.grabbedComponent?.let(renderedComponents::add)

        // need to render controls first as they would overlay other components
        renderedComponents.filterIsInstance<ControlComponent>().forEach {
            renderControlComponent(toRenderSpace(it.pos), it.controlledGate!!)
        }

        QCComposer.selectedComponents.forEach {
            ctx.color(SELECTION_COLOR)
            ctx.fillSquare(toRenderSpace(it.pos) - SELECTION_INDICATOR_WIDTH, GATE_SIZE + SELECTION_INDICATOR_WIDTH * 2.0)
        }

        renderedComponents.filter { it !is ControlComponent }.forEach {
            when(it) {
                is GateComponent -> renderGate(toRenderSpace(it.pos), it.type)
                is MeasurementComponent -> renderMeasurementComponent(toRenderSpace(it.pos))
            }
        }
    }

    fun renderGate(pos: Vector, gateType: GateType) {
        ctx.color(GATE_COLOR)
        ctx.fillSquare(pos, GATE_SIZE)

        ctx.color("black")
        ctx.font = "15px sans-serif"
        ctx.fillTextCentered(gateType.representation, pos.componentPosToCenter() + Vector(y = 2.0)) // TODO: baseline offset

    }

    private fun renderControlComponent(pos: Vector, controlledGate: GateComponent) {
        ctx.color(CIRCUIT_COLOR)

        val center = pos.componentPosToCenter()
        ctx.drawLine(center, controlledGate.getRenderedCenter())
        ctx.fillCircle(center, radius = GATE_SIZE / 8.0)
    }

    fun renderMeasurementComponent(pos: Vector) {
        ctx.color("#acacac")
        ctx.fillSquare(pos, GATE_SIZE)

        val circleCenter = pos.componentPosToCenter() + Vector(y = MEASUREMENT_SYMBOL_RADIUS / 2.0)
        val indicatorEnd = circleCenter + Vector(cos(1.0/3.0* PI), -sin(1.0/3.0* PI)).normalise() * MEASUREMENT_SYMBOL_RADIUS * 1.5

        // measurement symbol
        ctx.color("black")
        ctx.lineWidth = 1.5
        ctx.beginPath()
        ctx.arc(circleCenter.x, circleCenter.y, MEASUREMENT_SYMBOL_RADIUS, PI, 0.0)
        ctx.moveTo(circleCenter.x, circleCenter.y)
        ctx.lineTo(indicatorEnd.x, indicatorEnd.y)
        ctx.stroke()
    }

    private fun Vector.componentPosToCenter() = this + (GATE_SIZE / 2.0)
    private fun CircuitComponent.getRenderedCenter() = toRenderSpace(this.pos).componentPosToCenter()

    /**
     * Takes a vector in world space and converts it to grid space before rendering
     */
    fun toRenderSpace(pos: Vector) = pos * QCComposer.GRID_SIZE
}