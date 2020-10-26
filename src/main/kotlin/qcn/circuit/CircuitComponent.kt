package qcn.circuit

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import qcn.QCComposer
import util.math.Rectangle
import util.math.Vector

@Serializable
abstract class CircuitComponent() {

    abstract var pos : Vector
    @Transient
    open val selectable = false

    open fun isValidPosition(newPos: Vector) = true

    operator fun contains(v: Vector) = isWorldPosOnElement(v)
    fun isWorldPosOnElement(v: Vector) = v in worldSpaceRect()
    private fun worldSpaceRect() =
        Rectangle(pos, QCComposer.GATE_SIZE_WORLD_SPACE, QCComposer.GATE_SIZE_WORLD_SPACE)

}