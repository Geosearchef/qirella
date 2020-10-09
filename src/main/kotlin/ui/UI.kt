package ui

import Composer
import Composer.circuit
import Composer.grabbedComponent
import circuit.GateComponent
import circuit.GateType
import circuit.MeasurementComponent
import input.Input
import org.w3c.dom.events.MouseEvent
import rendering.Rendering
import util.math.Rectangle
import util.math.Vector

object UI {

    const val ADDABLE_GATE_SPACING = 15.0
    const val TOP_BAR_SIZE = Composer.GATE_SIZE + ADDABLE_GATE_SPACING * 2.0

    const val ACTION_WIDTH = 80.0
    const val ACTION_HEIGHT = 25.0
    const val ACTION_SPACING = 10.0

    // components
    val uiAddableComponents = HashMap<GateType, Rectangle>()
    val measurementComponent: Rectangle

    // selection interaction
    val actions = HashMap<Action, Rectangle>()

    // UI components
    init {
        GateType.values().forEachIndexed { i, gateType ->
            uiAddableComponents[gateType] = Rectangle(
                Vector(
                    x = i.toDouble() * (Composer.GATE_SIZE + ADDABLE_GATE_SPACING) + ADDABLE_GATE_SPACING,
                    y = ADDABLE_GATE_SPACING
                ),
                width = Composer.GATE_SIZE,
                height = Composer.GATE_SIZE
            )
        }

        val addableGatesWidth = uiAddableComponents.size.toDouble() * (Composer.GATE_SIZE + ADDABLE_GATE_SPACING) + ADDABLE_GATE_SPACING

        measurementComponent = Rectangle(
            Vector(
                x = addableGatesWidth + ADDABLE_GATE_SPACING,
                y = ADDABLE_GATE_SPACING
            ),
            width = Composer.GATE_SIZE,
            height = Composer.GATE_SIZE
        )
    }



    // Actions, relative to start of action area
    init {
        actions[Action.DELETE] = Rectangle(Vector(0.0, ACTION_SPACING), ACTION_WIDTH, ACTION_HEIGHT)
        actions[Action.LOAD]   = Rectangle(Vector(ACTION_WIDTH + ACTION_SPACING, ACTION_SPACING), ACTION_WIDTH, ACTION_HEIGHT)
        actions[Action.SAVE]   = Rectangle(Vector(ACTION_WIDTH + ACTION_SPACING, ACTION_SPACING * 2.0 + ACTION_HEIGHT), ACTION_WIDTH, ACTION_HEIGHT)

        // Shift all actions to all to the right of the canvas
        val actionsWidth = (actions.values.map { it.pos.x + it.width }.max() ?: 0.0) + 10.0
        val actionsStartX = Rendering.width - actionsWidth

        actions.values.forEach { it.pos.x += actionsStartX }
    }

    fun onUIPressed(mousePosition: Vector, event: MouseEvent) {
        uiAddableComponents.entries.filter { mousePosition in it.value }.firstOrNull()?.let { it.key }?.let { gateType ->
            grabbedComponent = circuit.addComponent(GateComponent(pos = Input.toWorldSpace(mousePosition).round(), type = gateType))
        }

        if(mousePosition in measurementComponent) {
            grabbedComponent = circuit.addComponent(MeasurementComponent(Input.toWorldSpace(mousePosition).round()))
        }

        actions.entries.filter { mousePosition in it.value }.firstOrNull()?.let { it.key }?.let { action ->
            action.onAction(Composer.selectedComponents)
        }
    }


}