package input

import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import scene.Scene


object Input {
    const val KEY_TAB = 9 // WTF JavaScript, why are there no constants defined for this?

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

    private fun onKeyDown(event: Event) {
        if (event !is KeyboardEvent) throw RuntimeException("Event of wrong type")

        if(event.keyCode == KEY_TAB) {
            Scene.switchScene()
            event.preventDefault()
            return
        }

        Scene.currentScene.input.onKeyDown(event)
    }

    private fun onKeyUp(event: Event) {
        if (event !is KeyboardEvent) throw RuntimeException("Event of wrong type")

        if(event.keyCode == KEY_TAB) {
            event.preventDefault()
            return
        }

        Scene.currentScene.input.onKeyUp(event)
    }



    fun init(canvas: HTMLCanvasElement) {
        with(canvas) {
            addEventListener("contextmenu", Event::preventDefault)
            addEventListener("mousemove", ::onMouseMove)
            addEventListener("mousedown", ::onMouseDown)
            addEventListener("mouseup", ::onMouseUp)
            addEventListener("wheel", ::onMouseWheel)
        }
        with(window) {
            addEventListener("keydown", input.Input::onKeyDown)
            addEventListener("keyup", input.Input::onKeyUp)
        }
    }
}