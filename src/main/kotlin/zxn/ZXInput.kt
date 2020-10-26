package zxn

import Qirella
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import qcn.QCComposer
import scene.Scene
import util.math.Vector

object ZXInput : Scene.SceneInput {
    var mousePosition = Vector()
    var isMapMoving = false


    override fun onMouseMove(event: MouseEvent) {
        mousePosition = Vector(event.offsetX, event.offsetY)

        if(isMapMoving) {
            ZXComposer.offset += Vector(
                js("event.movementX") as Double / QCComposer.scale,  // not supported by internet explorer, therefore not exposed
                js("event.movementY") as Double / QCComposer.scale
            )
        }

        if(isMapMoving) {
            Qirella.requestRender()
        }
    }

    override fun onMouseDown(event: MouseEvent) {
        onComposerPressed(event)
    }

    private fun onComposerPressed(event: MouseEvent) {
        isMapMoving = true
    }

    override fun onMouseUp(event: MouseEvent) {
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