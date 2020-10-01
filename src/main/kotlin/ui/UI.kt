package ui

import Composer
import Composer.circuit
import Composer.grabbedComponent
import circuit.GateComponent
import circuit.GateType
import circuit.MeasurementComponent
import input.Input
import org.w3c.dom.events.MouseEvent
import util.math.Rectangle
import util.math.Vector

object UI {

    const val ADDABLE_GATE_SPACING = 15.0
    const val TOP_BAR_SIZE = Composer.GATE_SIZE + ADDABLE_GATE_SPACING * 2.0

    val uiAddableComponents = HashMap<GateType, Rectangle>()
    val measurementComponent: Rectangle


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

    fun onUIPressed(mousePosition: Vector, event: MouseEvent) {
        uiAddableComponents.entries.filter { mousePosition in it.value }.firstOrNull()?.let {
            grabbedComponent = circuit.addComponent(GateComponent(pos = Input.toWorldSpace(mousePosition).round(), type = it.key))
        }

        if(mousePosition in measurementComponent) {
            grabbedComponent = circuit.addComponent(MeasurementComponent(Input.toWorldSpace(mousePosition).round()))
        }
    }
}