package input

import Composer
import Composer.circuit
import Composer.grabbedComponent
import Composer.grabbedOrigin
import circuit.CircuitComponent
import circuit.ControlComponent
import circuit.GateComponent
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import ui.UI
import util.math.Vector

object Input {
    var mousePosition = Vector()
    var isMapMoving = false



    private fun onMouseMove(event: Event) {
        if(event !is MouseEvent) throw RuntimeException("Event of wrong type")

        mousePosition = Vector(event.offsetX, event.offsetY)

        grabbedComponent?.let {
            circuit.setComponentPosition(it, (toWorldSpace(mousePosition) - Composer.GATE_SIZE_WORLD_SPACE / 2).round())// TODO: round correctly

            if(it.pos != grabbedOrigin) { // component moved, deselect
                Composer.selectedComponents.remove(it)
            }
        }


        if(isMapMoving) {
            Composer.offset += Vector(
                js("event.movementX") as Double / Composer.GRID_SIZE / Composer.scale,  // not supported by internet explorer, therefore not exposed
                js("event.movementY") as Double / Composer.GRID_SIZE / Composer.scale
            )
        }

        if(isMapMoving || grabbedComponent != null) {
            Composer.requestRender()
        }
    }



    private fun onMouseDown(event: Event) {
        if (event !is MouseEvent) throw RuntimeException("Event of wrong type")

        if(mousePosition.y < UI.TOP_BAR_SIZE) {
            UI.onUIPressed(mousePosition, event)
        } else {
            onComposerPressed(event)
        }

        Composer.requestRender()
    }

    private fun onComposerPressed(event: MouseEvent) {
        val worldPos = toWorldSpace(mousePosition)

        if (event.button.toInt() == 0) {
            val clickedComponent = getCircuitElementForWorldPos(worldPos)

            if(!event.ctrlKey) {
                Composer.deselectAllComponents()
            }

            if (!event.shiftKey) {
                // Gate dragging
                if (clickedComponent != null) {
                    grabbedComponent = clickedComponent
                    grabbedOrigin = clickedComponent.pos.clone()

                    Composer.selectComponent(clickedComponent)
                }
            } else {
                // Control creation
                if (clickedComponent is GateComponent) {
                    val newControl = clickedComponent.createControl()
                    circuit.addComponent(newControl)
                    grabbedComponent = newControl
                }
            }
        }

        // Deletion
        if (event.button.toInt() == 2) {
            circuit.components.filter { worldPos in it }.forEach { circuit.removeComponent(it) }
        }

        if (grabbedComponent == null) {
            isMapMoving = true
        }
    }


    private fun onMouseUp(event: Event) {
        if (event !is MouseEvent) throw RuntimeException("Event of wrong type")

        // Drop grabbed circuit component
        grabbedComponent?.let { grabbedComponent ->

            circuit.components.find { c -> c != grabbedComponent && c.pos == grabbedComponent.pos }?.let { blockingComponent ->
                if(blockingComponent is ControlComponent) {
                    // blocked by control, remove control
                    blockingComponent.removeFromParent()
                    circuit.removeComponent(blockingComponent)
                } else {
                    //Dropped position is already blocked, undo move
                    if(grabbedOrigin == null) {
                        circuit.removeComponent(grabbedComponent)
                    } else {
                        grabbedComponent.pos = grabbedOrigin!!
                    }
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
    fun toWorldSpace(v: Vector): Vector {
        return (v / Composer.GRID_SIZE / Composer.scale) - Composer.offset
    }

    private fun getCircuitElementForWorldPos(worldPos: Vector): CircuitComponent? {
        return circuit.components.filter { worldPos in it }.firstOrNull()
    }
}