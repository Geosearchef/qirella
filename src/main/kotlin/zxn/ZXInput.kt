package zxn

import Qirella
import input.Input.LEFT_MOUSE_BUTTON
import input.Input.RIGHT_MOUSE_BUTTON
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import scene.SceneInput
import util.math.Vector
import util.math.rectangleOf
import zxn.ZXComposer.deselectAllNodes
import zxn.ZXComposer.grabbedNode
import zxn.ZXComposer.network
import zxn.ZXComposer.selectNode
import zxn.ZXComposer.selectedNodes
import kotlin.math.pow

object ZXInput : SceneInput() {

    const val DRAG_TRESHOLD = 5.0

    var mousePositionWorld = Vector()
    var mouseMovementWorld = Vector()

    var isMapBeingDragged = false
    var mapDragStart = Vector()

    var selectionDragMode = false
    var selectionDragStart = Vector()

    var selectionAreaMode = false
    var selectionAreaStart = Vector()


    override fun onMouseMove(event: MouseEvent, isOnUI: Boolean) {
        mousePositionWorld = toWorldSpace(mousePosition)
        mouseMovementWorld = mouseMovement / ZXComposer.scale

        if(selectionDragMode && selectedNodes.size >= 2) {
            selectedNodes.forEach { it.pos += mouseMovementWorld }
        }
        grabbedNode?.let { network.setNodePosition(it, mousePositionWorld) }

        if(isMapBeingDragged) {
            ZXComposer.offset += mouseMovementWorld
        }

        if(selectionAreaMode) {
            deselectAllNodes()
            val selectionArea = rectangleOf(selectionAreaStart, mousePositionWorld)
            network.nodes.filter { it.pos in selectionArea }.forEach { selectNode(it) }
        }

        if(selectionAreaMode || isMapBeingDragged || selectionDragMode || grabbedNode != null) {
            Qirella.requestRender()
        }
    }

    override fun onMouseDown(event: MouseEvent, isOnUI: Boolean) {
        if(!isOnUI) {
            onComposerPressed(event)
        }

        Qirella.requestRender()
    }

    private fun onComposerPressed(event: MouseEvent) {
        val pressedNodes = network.nodes.filter { mousePositionWorld in it }


        if(event.button == LEFT_MOUSE_BUTTON) {

            if (event.ctrlKey) {
                TODO("wire creation not yet implemented")
                //pressedNodes.firstOrNull()?.let
            } else {
                pressedNodes.firstOrNull()?.let {
                    if(!selectedNodes.contains(it) && !event.shiftKey) {
                        deselectAllNodes()
                    }
                    grabbedNode = it
                    ZXComposer.selectNode(it)
                }

                selectionDragMode = true
                selectionDragStart = mousePosition
            }
        }

        if(event.button == LEFT_MOUSE_BUTTON && grabbedNode == null && selectedNodes.isEmpty()) {
            isMapBeingDragged = true
            mapDragStart = mousePosition
        }

        if(event.button == RIGHT_MOUSE_BUTTON) {
            selectionAreaMode = true
            selectionAreaStart = mousePositionWorld
        }
    }

    override fun onMouseUp(event: MouseEvent, isOnUI: Boolean) {
        grabbedNode = null
        isMapBeingDragged = false
        selectionDragMode = false
        selectionAreaMode = false

        val pressedNodes = network.nodes.filter { mousePositionWorld in it }

        if(event.button == RIGHT_MOUSE_BUTTON && (selectionDragStart - mousePosition).lengthSquared() < DRAG_TRESHOLD.pow(2)) {
            pressedNodes.forEach { network.removeNode(it) }
        }

        if(event.button == LEFT_MOUSE_BUTTON && (!isMapBeingDragged || (mapDragStart - mousePosition).lengthSquared() < DRAG_TRESHOLD.pow(2))) {
            if(!event.shiftKey) {
                deselectAllNodes()
                pressedNodes.firstOrNull()?.let { selectNode(it) }
            }
        }

        Qirella.requestRender()
    }

    override fun onMouseWheel(event: WheelEvent, isOnUI: Boolean) {

    }

    override fun onKeyDown(event: KeyboardEvent) {

    }

    override fun onKeyUp(event: KeyboardEvent) {

    }


    fun toWorldSpace(v: Vector): Vector {
        return (v / ZXComposer.scale) - ZXComposer.offset
    }
}