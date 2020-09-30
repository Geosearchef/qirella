package circuit

import util.math.Vector

class GateComponent(pos: Vector = Vector(), var type: GateType) : CircuitComponent(pos) {

    val controlComponents: MutableList<ControlComponent> = ArrayList<ControlComponent>()


    fun createControl() : ControlComponent {
        return ControlComponent(pos.clone(), this).also { controlComponents.add(it) }
    }
}