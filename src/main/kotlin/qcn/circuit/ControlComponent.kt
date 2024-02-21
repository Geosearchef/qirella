package qcn.circuit

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import util.math.Vector
import util.math.linalg.matrixOf

@Serializable
class ControlComponent(override var pos: Vector, @Serializable(with = ControlledGateSerializer::class) var controlledGate: GateComponent? = null) : CircuitComponent() {
// controlledGate is nullable due to being late initialized after deserialization and having to wait for deserialization of other components

    override val selectable = true

    fun removeFromParent() {
        controlledGate!!.controlComponents.remove(this)
    }

    override fun isValidPosition(newPos: Vector): Boolean {
        return newPos.x == controlledGate!!.pos.x
    }

}

// this only serializes the controlled gate as a position
// the deserialize afterward restores the reference to the controlledGate by searching based on the position
object ControlledGateSerializer : KSerializer<GateComponent?> {
    override val descriptor = Vector.serializer().descriptor

    override fun serialize(encoder: Encoder, value: GateComponent?) {
        require(value != null) // controlledGate is only nullable due to being late initialized
        Vector.serializer().serialize(encoder, value.pos)
    }

    override fun deserialize(decoder: Decoder): GateComponent? {
        return GateComponent("-", matrixOf(arrayOf()), "#000000", Vector.serializer().deserialize(decoder))
    }
}
