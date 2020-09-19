package rendering

import Composer
import Composer.GATE_SIZE
import Composer.GATE_SIZE_WORLD_SPACE
import Composer.GRID_SIZE
import circuit.CircuitComponent
import circuit.GateComponent
import circuit.GateType
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.CanvasTextAlign
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.RIGHT
import ui.UI
import ui.UI.TOP_BAR_SIZE
import util.math.Vector
import util.toDecimals

object Rendering {

    private var averageFrameTime = -1.0;

    private lateinit var ctx: CanvasRenderingContext2D
    private var width = 0.0
    private var height = 0.0


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
        ctx.fillText("Frame Time: ${averageFrameTime.toDecimals(3)} s  (${ (1.0 / averageFrameTime).toDecimals(1) } fps)", width - 20.0, 20.0);
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
        ctx.color("#4a4a4a")
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

        // Render register states
        //TODO:
    }

    private fun renderComponents() {
        val renderedComponents = ArrayList<CircuitComponent>(Composer.circuit.components)
        renderedComponents.addAll(Composer.circuit.components)
        Composer.grabbedComponent?.let(renderedComponents::add)

        for(component in renderedComponents) {
            renderComponent(toRenderSpace(component.pos), (component as? GateComponent)?.type)
        }
    }

    private fun renderComponent(pos: Vector, gateType: GateType?) {
        ctx.color("#278f42")
        ctx.fillSquare(pos, GATE_SIZE)

        gateType?.let {
            ctx.color("#000000")
            ctx.font = "15px sans-serif"
            ctx.fillTextCentered(it.representation, pos + (GATE_SIZE / 2.0) + Vector(y = 2.0)) // TODO: baseline offset
        }
    }


    private fun renderUI() {
        ctx.color("#cccccc")
        ctx.fillRect(0.0, 0.0, width, TOP_BAR_SIZE)

        UI.uiAddableComponents.forEach { renderComponent(it.value.pos, it.key) }
    }
}