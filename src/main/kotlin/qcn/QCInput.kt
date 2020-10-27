package qcn

import Qirella
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import qcn.QCComposer.circuit
import qcn.QCComposer.grabbedComponent
import qcn.QCComposer.grabbedOrigin
import qcn.circuit.CircuitComponent
import qcn.circuit.ControlComponent
import qcn.circuit.GateComponent
import scene.SceneInput
import util.math.Vector

object QCInput : SceneInput() {
    var isMapMoving = false


    override fun onMouseMove(event: MouseEvent, isOnUI: Boolean) {
        grabbedComponent?.let {
            circuit.setComponentPosition(it, (toWorldSpace(mousePosition) - QCComposer.GATE_SIZE_WORLD_SPACE / 2).round())// TODO: round correctly

            if(it.pos != grabbedOrigin) { // component moved, deselect
                QCComposer.selectedComponents.remove(it)
            }
        }

        if(isMapMoving) {
            QCComposer.offset += Vector(
                js("event.movementX") as Double / QCComposer.GRID_SIZE / QCComposer.scale,  // not supported by internet explorer, therefore not exposed
                js("event.movementY") as Double / QCComposer.GRID_SIZE / QCComposer.scale
            )
        }

        if(isMapMoving || grabbedComponent != null) {
            Qirella.requestRender()
        }
    }



    override fun onMouseDown(event: MouseEvent, isOnUI: Boolean) {
        if(! isOnUI) {
            onComposerPressed(event)
        }

        Qirella.requestRender()
    }

    private fun onComposerPressed(event: MouseEvent) {
        val worldPos = toWorldSpace(mousePosition)

        if (event.button.toInt() == 0) {
            val clickedComponent = getCircuitElementForWorldPos(worldPos)

            if(!event.ctrlKey) {
                QCComposer.deselectAllComponents()
            }

            if (!event.shiftKey) {
                // Gate dragging
                if (clickedComponent != null) {
                    grabbedComponent = clickedComponent
                    grabbedOrigin = clickedComponent.pos.clone()

                    QCComposer.selectComponent(clickedComponent)
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


    override fun onMouseUp(event: MouseEvent, isOnUI: Boolean) {
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

            QCComposer.grabbedComponent = null
            QCComposer.grabbedOrigin = null
        }


        isMapMoving = false

        Qirella.requestRender()
    }



    override fun onMouseWheel(event: WheelEvent, isOnUI: Boolean) {

    }

    /**
     * Converts a vector from SCREEN SPACE (canvas, not rendering/grid space) to world space
     */
    fun toWorldSpace(v: Vector): Vector {
        return (v / QCComposer.GRID_SIZE / QCComposer.scale) - QCComposer.offset
    }

    private fun getCircuitElementForWorldPos(worldPos: Vector): CircuitComponent? {
        return circuit.components.filter { worldPos in it }.firstOrNull()
    }
}