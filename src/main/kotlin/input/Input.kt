package input

import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import scene.Scene


object Input {
    private fun onMouseMove(event: Event) {
        if(event !is MouseEvent) throw RuntimeException("Event of wrong type")
        Scene.currentScene.input.onMouseMove(event)
    }

    private fun onMouseDown(event: Event) {
        if (event !is MouseEvent) throw RuntimeException("Event of wrong type")
        Scene.currentScene.input.onMouseDown(event)
    }

    private fun onMouseUp(event: Event) {
        if (event !is MouseEvent) throw RuntimeException("Event of wrong type")
        Scene.currentScene.input.onMouseUp(event)
    }

    private fun onMouseWheel(event: Event) {
        if (event !is WheelEvent) throw RuntimeException("Event of wrong type")
        Scene.currentScene.input.onMouseWheel(event)
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
}