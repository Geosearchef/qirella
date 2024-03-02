package qcn

import Qirella
import input.Input.LEFT_MOUSE_BUTTON
import input.Input.RIGHT_MOUSE_BUTTON
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import qcn.QCComposer.circuit
import qcn.QCComposer.grabbedComponent
import qcn.QCComposer.grabbedOrigin
import qcn.circuit.CircuitComponent
import qcn.circuit.ControlComponent
import qcn.circuit.GateComponent
import qcn.simulation.StatevectorSimulator
import scene.SceneInput
import util.math.Vector
import util.toDecimals

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
            QCComposer.offset += mouseMovement / QCComposer.GRID_SIZE / QCComposer.scale
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

        if (event.button == LEFT_MOUSE_BUTTON) {
            val clickedComponent = getCircuitElementForWorldPos(worldPos)

            if(!event.shiftKey) {
                QCComposer.deselectAllComponents()
            }

            if (!event.ctrlKey) {
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
        if (event.button == RIGHT_MOUSE_BUTTON) {
            circuit.components.filter { worldPos in it }.forEach { circuit.removeComponent(it) }
            QCComposer.resimulate()
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

            Qirella.requestRender()
            QCComposer.resimulate()
        }


        isMapMoving = false

        Qirella.requestRender()
    }

    override fun onMouseWheel(event: WheelEvent, isOnUI: Boolean) {}

    override fun onKeyDown(event: KeyboardEvent) {
        if(event.keyCode == 13) {
//            val result = StatevectorSimulator.singleShotRun(circuit)
//            println(result.classicalRegisters)
            val results = StatevectorSimulator.multiShotRun(circuit, shots = QCComposer.configuredShotCount)
            results.entries.forEach { println("${it.key}: ${it.value.toDecimals(3)}") }
        }
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