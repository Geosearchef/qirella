package qcn.circuit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import util.math.Vector

@Serializable
class GateComponent(override var pos: Vector = Vector(), @SerialName("gateType") var type: GateType) : CircuitComponent() {

    override val selectable = true

    // late initialized after waiting for deserialization of other components!
    @Transient val controlComponents: MutableList<ControlComponent> = ArrayList<ControlComponent>()


    fun createControl() : ControlComponent {
        return ControlComponent(pos.clone(), this).also { controlComponents.add(it) }
    }
}