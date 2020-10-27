package zxn

import Qirella
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
    }

    private fun onComposerPressed(event: MouseEvent) {

        network.nodes.find { mousePositionWorld in it }?.let { grabbedNode ->
            if(event.shiftKey) {

            } else {
                ZXComposer.grabbedNode = grabbedNode
            }
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