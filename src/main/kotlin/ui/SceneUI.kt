package ui

import org.w3c.dom.events.MouseEvent
import rendering.Rendering
import rendering.color
import util.math.Vector

abstract class SceneUI(val width: Int, val height: Int) {

    abstract val TOP_BAR_SIZE: Double

    open fun render() {
        Rendering.ctx.color("#cccccc")
        Rendering.ctx.fillRect(0.0, 0.0, Rendering.width, TOP_BAR_SIZE)
    }
    abstract fun onUIPressed(mousePosition: Vector, event: MouseEvent)
    abstract fun isMouseEventOnUI(mousePosition: Vector): Boolean
}