package input

import Composer
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import util.Vector

object Input {
    var mousePosition = Vector()
    var isMapMoving = false

    private fun onMouseMove(event: Event) {
        if(event !is MouseEvent) throw RuntimeException("Event of wrong type")

        mousePosition = Vector(event.offsetX, event.offsetY)

        if(isMapMoving) {
            Composer.offset += Vector(
                js("event.movementX") as Double / Composer.scale,  // not supported by internet explorer, therefore not exposed
                js("event.movementY") as Double / Composer.scale
            )
        }
    }

    private fun onMouseDown(event: Event) {
        if (event !is MouseEvent) throw RuntimeException("Event of wrong type")

        isMapMoving = true
    }

    private fun onMouseUp(event: Event) {
        if (event !is MouseEvent) throw RuntimeException("Event of wrong type")

        isMapMoving = false
    }

    private fun onMouseWheel(event: Event) {
        if (event !is WheelEvent) throw RuntimeException("Event of wrong type")
    }

    fun init(canvas: HTMLCanvasElement) {
        with(canvas) {
            addEventListener("contextmenu", Event::preventDefault)
            addEventListener("mousemove", ::onMouseMove)
            addEventListener("mousedown", ::onMouseDown)
            addEventListener("mouseup", ::onMouseUp)
            addEventListener("wheel", ::onMouseWheel)
        }
    }
}