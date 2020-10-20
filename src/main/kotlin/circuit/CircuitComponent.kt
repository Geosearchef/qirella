package circuit

import Composer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
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
        Rectangle(pos, Composer.GATE_SIZE_WORLD_SPACE, Composer.GATE_SIZE_WORLD_SPACE)

}