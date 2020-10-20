package rendering

import Composer
import Composer.GATE_SIZE
import Composer.GATE_SIZE_WORLD_SPACE
import Composer.GRID_SIZE
import circuit.*
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.RIGHT
import ui.Action
import ui.UI
import ui.UI.Companion.TOP_BAR_SIZE
import util.math.Rectangle
import util.math.Vector
import util.toDecimals
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object Rendering {

    const val GATE_COLOR = "#278f42"
    const val CIRCUIT_COLOR = "#4a4a4a"

    const val MEASUREMENT_SYMBOL_RADIUS = 15.0

    const val SELECTION_COLOR = "#27408f"
    const val SELECTION_INDICATOR_WIDTH = 5.0

    const val ACTION_BUTTON_COLOR = "#dddddd"


    private var averageFrameTime = -1.0;

    private lateinit var ctx: CanvasRenderingContext2D
    var width = 0.0
    var height = 0.0


    fun render(delta: Double, canvas: HTMLCanvasElement) {
        canvas.resizeCanvas()

        if(!Composer.renderRequested && !Composer.continousRendering) {
            return
        }
        Composer.renderRequested = false

        ctx = canvas.getContext("2d") as CanvasRenderingContext2D
        width = canvas.width.toDouble()
        height = canvas.height.toDouble()
        ctx.clearRect(0.0, 0.0, width, height)

        renderCircuit()
        renderUI()

        // redner frametime
        ctx.fillStyle = "#000000"
        ctx.font = "10px sans-serif"
        ctx.textAlign = CanvasTextAlign.RIGHT
        averageFrameTime = if(averageFrameTime == -1.0) delta else averageFrameTime * 0.95 + delta * 0.05;
        ctx.fillText("Frame Time: ${averageFrameTime.toDecimals(3)} s  (${ (1.0 / averageFrameTime).toDecimals(1) } fps)", width - 20.0, height - 20.0);
    }



    private fun renderCircuit() {
        ctx.scale(Composer.scale, Composer.scale)
        ctx.translate(Composer.offset.x * GRID_SIZE, Composer.offset.y * GRID_SIZE)

        renderRegisters()

        renderComponents()

        ctx.setIdentityMatrix()
    }

    private fun renderRegisters() {
        val elements = Composer.circuit.components
        if (elements.isEmpty()) {
            return
        }

        val firstGate: Double = elements.map { it.pos.x }.min()!!
        val lastGate: Double = elements.map { it.pos.x }.max()!!

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
        Composer.circuit.getUsedRegisters().forEach { register ->
            //TODO: extract rendering procedure
            val registerPos = (toRenderSpace(Vector(firstGate - GATE_SIZE_WORLD_SPACE, register.toDouble() + GATE_SIZE_WORLD_SPACE / 2.0))
                    + Vector(-35.0, 0.0))
            ctx.fillTextCentered("|", registerPos + Vector(y = -1.0))
            ctx.fillTextCentered("0⟩", registerPos + Vector(x = 11.0))
        }
    }

    private fun renderComponents() {
        val renderedComponents = ArrayList<CircuitComponent>(Composer.circuit.components)
        renderedComponents.addAll(Composer.circuit.components)
        Composer.grabbedComponent?.let(renderedComponents::add)

        // need to render controls first as they would overlay other components
        renderedComponents.filterIsInstance<ControlComponent>().forEach {
            renderControlComponent(toRenderSpace(it.pos), it.controlledGate!!)
        }

        Composer.selectedComponents.forEach {
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

    private fun renderGate(pos: Vector, gateType: GateType) {
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

    private fun renderMeasurementComponent(pos: Vector) {
        ctx.color("#acacac")
        ctx.fillSquare(pos, GATE_SIZE)

        val circleCenter = pos.componentPosToCenter() + Vector(y = MEASUREMENT_SYMBOL_RADIUS / 2.0)
        val indicatorEnd = circleCenter + Vector(cos(1.0/3.0*PI), -sin(1.0/3.0*PI)).normalise() * MEASUREMENT_SYMBOL_RADIUS * 1.5

        // measurement symbol
        ctx.color("black")
        ctx.lineWidth = 1.5
        ctx.beginPath()
        ctx.arc(circleCenter.x, circleCenter.y, MEASUREMENT_SYMBOL_RADIUS, PI, 0.0)
        ctx.moveTo(circleCenter.x, circleCenter.y)
        ctx.lineTo(indicatorEnd.x, indicatorEnd.y)
        ctx.stroke()
    }

    private fun renderAction(rect: Rectangle, action: Action) {
        ctx.color(ACTION_BUTTON_COLOR)
        ctx.fillRect(rect.pos, rect.width, rect.height)

        ctx.color("black")
        ctx.fillTextCentered(action.representation, rect.center + Vector(y=2.0))
    }


    private fun renderUI() {
        ctx.color("#cccccc")
        ctx.fillRect(0.0, 0.0, width, TOP_BAR_SIZE)

        UI.uiAddableComponents.forEach { renderGate(it.value.pos, it.key) }
        UI.measurementComponent.let { renderMeasurementComponent(it.pos) }
        UI.actions.forEach { renderAction(it.value, it.key) }
    }

    private fun Vector.componentPosToCenter() = this + (GATE_SIZE / 2.0)
    private fun CircuitComponent.getRenderedCenter() = toRenderSpace(this.pos).componentPosToCenter()
}