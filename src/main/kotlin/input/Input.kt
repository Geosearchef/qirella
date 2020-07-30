package input

import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import util.Vector

object Input {
    var mousePosition = Vector()

    fun onMouseMove(event: Event) {
        val mouseEvent = event as MouseEvent

        mousePosition = Vector(event.offsetX, event.offsetY)
        println(mousePosition)
    }
}