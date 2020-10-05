package circuit

import util.math.Vector

class GateComponent(pos: Vector = Vector(), var type: GateType) : CircuitComponent(pos) {

    override val selectable = true

    val controlComponents: MutableList<ControlComponent> = ArrayList<ControlComponent>()


    fun createControl() : ControlComponent {
        return ControlComponent(pos.clone(), this).also { controlComponents.add(it) }
    }
}