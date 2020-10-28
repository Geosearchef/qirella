package zxn

import Qirella
import input.Input.LEFT_MOUSE_BUTTON
import input.Input.RIGHT_MOUSE_BUTTON
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import qcn.QCComposer
import scene.SceneInput
import util.math.Vector
import zxn.ZXComposer.grabbedNode
import zxn.ZXComposer.network

object ZXInput : SceneInput() {
    var mousePositionWorld = Vector()
    var isMapMoving = false


    override fun onMouseMove(event: MouseEvent, isOnUI: Boolean) {
        mousePositionWorld = toWorldSpace(mousePosition)

        grabbedNode?.let { network.setNodePosition(it, mousePositionWorld) }

        if(isMapMoving) {
            ZXComposer.offset += Vector(
                js("event.movementX") as Double / QCComposer.scale,  // not supported by internet explorer, therefore not exposed
                js("event.movementY") as Double / QCComposer.scale
            )
        }

        if(isMapMoving || grabbedNode != null) {
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


        if(event.button.toInt() == LEFT_MOUSE_BUTTON) {
            if (event.shiftKey) {
                TODO("wire creation not yet implemented")
                //pressedNodes.firstOrNull()?.let
            } else {
                pressedNodes.firstOrNull()?.let {
                    ZXComposer.grabbedNode = it
                }
            }
        }

        if(event.button.toInt() == RIGHT_MOUSE_BUTTON) {
            pressedNodes.forEach { network.removeNode(it) }
        }

        if(grabbedNode == null) {
            isMapMoving = true
        }
    }

    override fun onMouseUp(event: MouseEvent, isOnUI: Boolean) {
        grabbedNode = null
        isMapMoving = false
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