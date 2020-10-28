package qcn.ui

import org.w3c.dom.events.MouseEvent
import qcn.QCComposer
import qcn.QCComposer.circuit
import qcn.QCComposer.grabbedComponent
import qcn.QCInput
import qcn.circuit.GateComponent
import qcn.circuit.GateType
import qcn.circuit.MeasurementComponent
import qcn.rendering.QCRenderer
import ui.SceneUI
import util.math.Rectangle
import util.math.Vector

class QCUI(width: Int, height: Int): SceneUI(width, height) {

    val ADDABLE_GATE_SPACING = 15.0
    val ACTION_WIDTH = 50.0
    val ACTION_HEIGHT = 25.0
    val ACTION_SPACING = 10.0

    override val TOP_BAR_SIZE = QCComposer.GATE_SIZE + ADDABLE_GATE_SPACING * 2.0

    // components
    private val uiAddableComponents = HashMap<GateType, Rectangle>()
    private val measurementComponent: Rectangle

    // selection interaction
    private val actions = HashMap<QCAction, Rectangle>()

    // UI components
    init {
        GateType.values().filter { it.placable }.forEachIndexed { i, gateType ->
            uiAddableComponents[gateType] = Rectangle(
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
        actions[QCAction.DELETE] = Rectangle(Vector(0.0, ACTION_SPACING), ACTION_WIDTH, ACTION_HEIGHT)
        actions[QCAction.LOAD]   = Rectangle(Vector(ACTION_WIDTH + ACTION_SPACING, ACTION_SPACING), ACTION_WIDTH, ACTION_HEIGHT)
        actions[QCAction.SAVE]   = Rectangle(Vector(ACTION_WIDTH + ACTION_SPACING, ACTION_SPACING * 2.0 + ACTION_HEIGHT), ACTION_WIDTH, ACTION_HEIGHT)

        // Shift all actions to all to the right of the canvas
        val actionsWidth = (actions.values.map { it.x + it.width }.maxOrNull() ?: 0.0) + 10.0
        val actionsStartX = this.width - actionsWidth

        actions.values.forEach { it.x += actionsStartX }
    }

    override fun onUIPressed(mousePosition: Vector, event: MouseEvent) {
        uiAddableComponents.entries.filter { mousePosition in it.value }.firstOrNull()?.key?.let { gateType ->
            grabbedComponent = circuit.addComponent(GateComponent(pos = QCInput.toWorldSpace(mousePosition).round(), type = gateType))
        }

        if(mousePosition in measurementComponent) {
            grabbedComponent = circuit.addComponent(MeasurementComponent(QCInput.toWorldSpace(mousePosition).round()))
        }

        actions.entries.filter { mousePosition in it.value }.firstOrNull()?.let { it.key }?.let { action ->
            action.onAction()
        }
    }

    override fun render() {
        super.render()

        uiAddableComponents.forEach { QCRenderer.renderGate(it.value.pos, it.key) }
        measurementComponent.let { QCRenderer.renderMeasurementComponent(it.pos) }
        actions.forEach { renderAction(it.value, it.key) }
    }

    override fun isMouseEventOnUI(mousePosition: Vector): Boolean = mousePosition.y < TOP_BAR_SIZE
}