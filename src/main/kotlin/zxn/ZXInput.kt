package zxn

import Qirella
import input.Input.KEY_A
import input.Input.KEY_C
import input.Input.KEY_D
import input.Input.KEY_G
import input.Input.KEY_N
import input.Input.LEFT_MOUSE_BUTTON
import input.Input.RIGHT_MOUSE_BUTTON
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import scene.SceneInput
import util.math.Vector
import util.math.rectangleOf
import zxn.ZXComposer.deselectAllNodes
import zxn.ZXComposer.grabOffset
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

        if(selectionDragMode && selectedNodes.size >= 2 && !selectionAreaMode) {
            selectedNodes.forEach { it.pos += mouseMovementWorld }
        }
        grabbedNode?.let { network.setNodePosition(it, (if(!event.ctrlKey) (mousePositionWorld + grabOffset) else ((mousePositionWorld + grabOffset) / 20.0).round() * 20.0)) }

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
                    grabOffset = it.pos - mousePositionWorld
                    ZXComposer.selectNode(it)
                }

                selectionDragMode = true
                selectionDragStart = mousePosition
            }
        }

        if(event.button == LEFT_MOUSE_BUTTON && !event.shiftKey && selectedNodes.size < 2 && grabbedNode == null) {
            isMapBeingDragged = true
            mapDragStart = mousePosition
        }

        if(event.button == LEFT_MOUSE_BUTTON && event.shiftKey) {
            selectionAreaMode = true
            selectionAreaStart = mousePositionWorld
        }
    }

    override fun onMouseUp(event: MouseEvent, isOnUI: Boolean) {
        if(isOnUI) {
            return
        }

        grabbedNode = null
        isMapBeingDragged = false
        selectionDragMode = false
        selectionAreaMode = false

        val pressedNodes = network.nodes.filter { mousePositionWorld in it }

        if(event.button == RIGHT_MOUSE_BUTTON) {
            pressedNodes.forEach { ZXComposer.removeNode(it) }
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
        if(event.keyCode == KEY_C) {
            ZXComposer.createSelectedWires()
        }
        if(event.keyCode == KEY_D) {
            ZXComposer.deleteSelectedWires()
        }
        if(event.keyCode == KEY_A) {
            ZXComposer.realignNetwork()
        }
        if(event.keyCode == KEY_G) {
            ZXComposer.greyscaleMode = !ZXComposer.greyscaleMode
        }
        if(event.keyCode == KEY_N) {
            network.clear()
        }

        Qirella.requestRender()
    }

    override fun onKeyUp(event: KeyboardEvent) {

    }


    fun toWorldSpace(v: Vector): Vector {
        return (v / ZXComposer.scale) - ZXComposer.offset
    }
}