package ui

import Composer
import circuit.GateType
import util.math.Rectangle
import util.math.Vector

object UI {

    const val ADDABLE_GATE_SPACING = 15.0
    const val TOP_BAR_SIZE = Composer.GATE_SIZE + ADDABLE_GATE_SPACING * 2.0

    val uiAddableComponents = HashMap<GateType, Rectangle>()


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
    }
}