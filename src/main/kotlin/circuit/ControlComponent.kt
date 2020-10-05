package circuit

import util.math.Vector

class ControlComponent(pos: Vector, var controlledGate: GateComponent) : CircuitComponent(pos) {

    override val selectable = true

    fun removeFromParent() {
        controlledGate.controlComponents.remove(this)
    }

    override fun isValidPosition(newPos: Vector): Boolean {
        return newPos.x == controlledGate.pos.x
    }

}