package qcn.circuit

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import qcn.rendering.QCRenderer
import util.math.Vector
import util.math.linalg.Matrix

@Serializable
open class GateComponent(val representation: String, val matrix : Matrix, val color: String = QCRenderer.GATE_COLOR, override var pos: Vector = Vector()) : CircuitComponent() {

    override val selectable = true

    // late initialized after waiting for deserialization of other components!
    @Transient val controlComponents: MutableList<ControlComponent> = ArrayList<ControlComponent>()


    fun createControl() : ControlComponent {
        return ControlComponent(pos.clone(), this).also { controlComponents.add(it) }
    }
}