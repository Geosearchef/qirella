package scene

import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import util.math.Vector

abstract class SceneInput {
    var mousePosition: Vector = Vector()
    var isOnUI: Boolean = false

    fun sealedOnMouseMove(event: MouseEvent) {
        mousePosition = Vector(event.offsetX, event.offsetY)
        isOnUI = Scene.currentScene.uiManager.getUI().isMouseEventOnUI(mousePosition)
        onMouseMove(event, isOnUI)
    }
    fun sealedOnMouseDown(event: MouseEvent) {
        onMouseDown(event, isOnUI)

        if(isOnUI) {
            Scene.currentScene.uiManager.getUI().onUIPressed(mousePosition, event)
        }
    }
    fun sealedOnMouseUp(event: MouseEvent) {
        onMouseUp(event, isOnUI)
    }
    fun sealedOnMouseWheel(event: WheelEvent) {
        onMouseWheel(event, isOnUI)
    }

    open fun onMouseMove(event: MouseEvent, isOnUI: Boolean) {}
    open fun onMouseDown(event: MouseEvent, isOnUI: Boolean) {}
    open fun onMouseUp(event: MouseEvent, isOnUI: Boolean) {}
    open fun onMouseWheel(event: WheelEvent, isOnUI: Boolean) {}
    open fun onKeyDown(event: KeyboardEvent) {}
    open fun onKeyUp(event: KeyboardEvent) {}
}