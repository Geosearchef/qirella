package input

import Composer
import circuit.GateComponent
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import ui.UI
import util.Vector

object Input {
    var mousePosition = Vector()
    var isMapMoving = false



    private fun onMouseMove(event: Event) {
        if(event !is MouseEvent) throw RuntimeException("Event of wrong type")

        mousePosition = Vector(event.offsetX, event.offsetY)

        Composer.grabbedComponent?.pos =
            (toWorldSpace(mousePosition) - Composer.GATE_SIZE_WORLD_SPACE / 2)
                .round() // TODO: round correctly

        if(isMapMoving) {
            Composer.offset += Vector(
                js("event.movementX") as Double / Composer.GRID_SIZE / Composer.scale,  // not supported by internet explorer, therefore not exposed
                js("event.movementY") as Double / Composer.GRID_SIZE / Composer.scale
            )
        }

        if(isMapMoving || Composer.grabbedComponent != null) {
            Composer.requestRender()
        }
    }



    private fun onMouseDown(event: Event) {
        if (event !is MouseEvent) throw RuntimeException("Event of wrong type")

        if(mousePosition.y < UI.TOP_BAR_SIZE) {
            // UI
            UI.uiAddableComponents.entries.filter { mousePosition in it.value }.firstOrNull()?.let {
                Composer.grabbedComponent = GateComponent(type = it.key)
                    .also { Composer.circuit.components.add(it) }
            }

        } else {
            val worldPos = toWorldSpace(mousePosition)

            if(event.button.toInt() == 0) {
                Composer.circuit.components
                    .filter { worldPos in it }
                    .firstOrNull()
                    ?.let {
                        Composer.grabbedComponent = it
                        Composer.grabbedOrigin = it.pos.clone()
                    }
            } else if (event.button.toInt() == 2) {
                Composer.circuit.components.removeAll { worldPos in it }
            }

            if(Composer.grabbedComponent == null) {
                isMapMoving = true
            }
        }

        Composer.requestRender()
    }



    private fun onMouseUp(event: Event) {
        if (event !is MouseEvent) throw RuntimeException("Event of wrong type")

        // Drop grabbed circuit component
        Composer.grabbedComponent?.let { grabbedComponent ->
            //Dropped position is already blocked
            if(Composer.circuit.components.count { c -> c.pos == grabbedComponent.pos } > 1) {
                when(Composer.grabbedOrigin) {
                    null -> Composer.circuit.components.remove(grabbedComponent)
                    else -> grabbedComponent.pos = Composer.grabbedOrigin!!
                }
            }

            Composer.grabbedComponent = null
            Composer.grabbedOrigin = null
        }

        isMapMoving = false

        Composer.requestRender()
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
        return (v / Composer.GRID_SIZE / Composer.scale) - Composer.offset
    }
}