package circuit

import Composer
import util.math.Rectangle
import util.math.Vector

open class CircuitComponent(var pos : Vector) {



    operator fun contains(v: Vector) = isWorldPosOnElement(v)
    fun isWorldPosOnElement(v: Vector) = v in worldSpaceRect()
    private fun worldSpaceRect() =
        Rectangle(pos, Composer.GATE_SIZE_WORLD_SPACE, Composer.GATE_SIZE_WORLD_SPACE)

}