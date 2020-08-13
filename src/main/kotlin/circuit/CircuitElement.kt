package circuit

import rendering.Rendering
import util.Rectangle
import util.Vector

open class CircuitElement(var pos : Vector) {



    fun isWorldPosOnElement(v: Vector) = v in worldSpaceRect()
    private fun worldSpaceRect() = Rectangle(pos, Rendering.GATE_SIZE_WORLD_SPACE, Rendering.GATE_SIZE_WORLD_SPACE)

}