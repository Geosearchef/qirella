package zxn

import Qirella
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import qcn.QCComposer
import scene.Scene
import util.math.Vector
import zxn.ZXComposer.grabbedNode
import zxn.ZXComposer.network

object ZXInput : Scene.SceneInput {
    var mousePosition = Vector()
    var mousePositionWorld = Vector()
    var isMapMoving = false


    override fun onMouseMove(event: MouseEvent) {
        mousePosition = Vector(event.offsetX, event.offsetY)
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

    override fun onMouseDown(event: MouseEvent) {
        onComposerPressed(event)
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

    override fun onMouseUp(event: MouseEvent) {
        grabbedNode = null
        isMapMoving = false
    }

    override fun onMouseWheel(event: WheelEvent) {

    }

    override fun onKeyDown(event: KeyboardEvent) {

    }

    override fun onKeyUp(event: KeyboardEvent) {

    }


    fun toWorldSpace(v: Vector): Vector {
        return (v / ZXComposer.scale) - ZXComposer.offset
    }
}