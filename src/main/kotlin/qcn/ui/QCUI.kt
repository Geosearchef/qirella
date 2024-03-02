package qcn.ui

import org.w3c.dom.events.MouseEvent
import qcn.QCComposer
import qcn.QCComposer.circuit
import qcn.QCComposer.grabbedComponent
import qcn.QCInput
import qcn.circuit.GateGenerator
import qcn.circuit.MeasurementComponent
import qcn.rendering.QCRenderer
import rendering.*
import rendering.Rendering.ctx
import ui.SceneUI
import util.math.Rectangle
import util.math.Vector
import util.toDecimals

class QCUI(width: Int, height: Int): SceneUI(width, height) {

    val ADDABLE_GATE_SPACING = 15.0
    val ACTION_WIDTH = 50.0
    val ACTION_HEIGHT = 25.0
    val ACTION_SPACING = 10.0

    override val TOP_BAR_SIZE = QCComposer.GATE_SIZE + ADDABLE_GATE_SPACING * 2.0

    companion object {
        const val BOT_BAR_SIZE = 198.0
        const val DEFAULT_FONT = "15px sans-serif"

        const val HIST_BAR_WIDTH = 30.0
        const val HIST_BAR_COLOR = "#2ca9d5"

    }

    private val BOT_BAR_Y = height - BOT_BAR_SIZE

    // components
    private val uiAddableComponents = HashMap<GateGenerator, Rectangle>()
    private val measurementComponent: Rectangle

    // selection interaction
    private val actions = HashMap<QCAction, Rectangle>()

    // bottom bar / simulation buttons

    // Top UI components
    init {
        GateGenerator.values().filter { it.placeable }.forEachIndexed { i, generator ->
            uiAddableComponents[generator] = Rectangle(
                Vector(
                    x = i.toDouble() * (QCComposer.GATE_SIZE + ADDABLE_GATE_SPACING) + ADDABLE_GATE_SPACING,
                    y = ADDABLE_GATE_SPACING
                ),
                width = QCComposer.GATE_SIZE,
                height = QCComposer.GATE_SIZE
            )
        }

        val addableGatesWidth = uiAddableComponents.size.toDouble() * (QCComposer.GATE_SIZE + ADDABLE_GATE_SPACING) + ADDABLE_GATE_SPACING

        measurementComponent = Rectangle(
            Vector(
                x = addableGatesWidth + ADDABLE_GATE_SPACING,
                y = ADDABLE_GATE_SPACING
            ),
            width = QCComposer.GATE_SIZE,
            height = QCComposer.GATE_SIZE
        )
    }

    // Actions, relative to start of action area
    init {
        addTopBarAction(QCAction.DELETE, Rectangle(Vector(0.0, ACTION_SPACING), ACTION_WIDTH, ACTION_HEIGHT))
        addTopBarAction(QCAction.LOAD, Rectangle(Vector(ACTION_WIDTH + ACTION_SPACING, ACTION_SPACING), ACTION_WIDTH, ACTION_HEIGHT))
        addTopBarAction(QCAction.SAVE, Rectangle(Vector(ACTION_WIDTH + ACTION_SPACING, ACTION_SPACING * 2.0 + ACTION_HEIGHT), ACTION_WIDTH, ACTION_HEIGHT))
    }

    // bot UI / simulation components
    init {
        actions[QCAction.INCREASE_SHOTS] = Rectangle(250.0, BOT_BAR_Y + 10.0, 30.0, 30.0)
        actions[QCAction.DECREASE_SHOTS] = Rectangle(250.0 + 40.0, BOT_BAR_Y + 10.0, 30.0, 30.0)
    }

    override fun onUIPressed(mousePosition: Vector, event: MouseEvent) {
        super.onUIPressed(mousePosition, event)

        uiAddableComponents.entries.filter { mousePosition in it.value }.firstOrNull()?.key?.let { gateGenerator ->
            grabbedComponent = circuit.addComponent(gateGenerator.generate(QCInput.toWorldSpace(mousePosition).round()))
        }

        if(mousePosition in measurementComponent) {
            grabbedComponent = circuit.addComponent(MeasurementComponent(QCInput.toWorldSpace(mousePosition).round(), 10 + circuit.getClassicalRegisters().size))
        }

        actions.filter { mousePosition in it.value }.forEach { it.key.onQCAction(QCComposer.selectedComponents) }
    }

    override fun render() {
        super.render()

        ctx.font = DEFAULT_FONT

        // Top bar
        uiAddableComponents.forEach { QCRenderer.renderGate(it.value.pos, it.key.dummy) }
        measurementComponent.let { QCRenderer.renderMeasurementComponent(it.pos) }

        //Bottom bar
        ctx.translate(0.0, BOT_BAR_Y)
        ctx.color("#cccccc")
        ctx.fillRect(0.0, 0.0, width.toDouble(), BOT_BAR_SIZE)

        ctx.color("black")
        ctx.font = "bold 15px sans-serif"
        ctx.fillText("Simulation", 15.0, 30.0)
        ctx.font = DEFAULT_FONT

        ctx.fillText("Shots: ${QCComposer.configuredShotCount}", 150.0, 30.0)

        // render simulation result
        val simulationResult = QCComposer.simulationResult
        val maxProb = simulationResult.values.maxOrNull() ?: 1.0
        val histTopCoord = 65.0
        val histHeight = 100.0
        val histBotCoord = histTopCoord + histHeight

        ctx.fillText(maxProb.toDecimals(2), 15.0, histTopCoord + 3.0)
        ctx.fillText("0.00", 15.0, histBotCoord + 3.0)


        val displayedEntries = simulationResult.entries.filter { simulationResult.size <= 8 || it.value > 0 }.sortedBy { it.key }
        var startX = 70.0
        for(resultEntry in displayedEntries) {
            val scaledProb = resultEntry.value / maxProb

            ctx.color("black")
            ctx.fillTextCentered(resultEntry.key, Vector(startX + HIST_BAR_WIDTH / 2, histBotCoord + 10.0))

            Rectangle(startX, histTopCoord + histHeight * (1.0 - scaledProb), HIST_BAR_WIDTH, histHeight * scaledProb).let {
                ctx.color(HIST_BAR_COLOR)
                ctx.fillRect(it)
                ctx.color("black")
                ctx.strokeRect(it)
            }

            startX += HIST_BAR_WIDTH * 2
        }


        ctx.setIdentityMatrix()

        // actions
        actions.forEach { renderAction(it.value, it.key) }
    }

    override fun isMouseEventOnUI(mousePosition: Vector): Boolean = mousePosition.y < TOP_BAR_SIZE || mousePosition.y > BOT_BAR_Y
}