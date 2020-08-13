package input

import Composer
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import rendering.Rendering
import util.Vector

object Input {
    var mousePosition = Vector()
    var isMapMoving = false

    private fun onMouseMove(event: Event) {
        if(event !is MouseEvent) throw RuntimeException("Event of wrong type")

        mousePosition = Vector(event.offsetX, event.offsetY)

        Composer.grabbedElement?.pos =
            (toWorldSpace(mousePosition) - Rendering.GATE_SIZE_WORLD_SPACE / 2)
                .round() // TODO: round correctly

        if(isMapMoving) {
            Composer.offset += Vector(
                js("event.movementX") as Double / Rendering.GRID_SIZE / Composer.scale,  // not supported by internet explorer, therefore not exposed
                js("event.movementY") as Double / Rendering.GRID_SIZE / Composer.scale
            )
        }
    }

    private fun onMouseDown(event: Event) {
        if (event !is MouseEvent) throw RuntimeException("Event of wrong type")

        val worldPos = toWorldSpace(mousePosition)

        Composer.circuit.elements
            .filter { it.isWorldPosOnElement(worldPos) }
            .firstOrNull()
            ?.let {
//                Composer.circuit.elements.remove(it)
                Composer.grabbedElement = it
            }


        if(Composer.grabbedElement == null) {
            isMapMoving = true
        }
    }

    private fun onMouseUp(event: Event) {
        if (event !is MouseEvent) throw RuntimeException("Event of wrong type")

        Composer.grabbedElement?.let {
//            Composer.circuit.elements.add(it)
            Composer.grabbedElement = null
        }

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

    /**
     * Converts a vector from SCREEN SPACE (canvas, not rendering/grid space) to world space
     */
    private fun toWorldSpace(v: Vector): Vector {
        return (v / Rendering.GRID_SIZE / Composer.scale) - Composer.offset
    }
}