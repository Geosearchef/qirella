package ui

import org.w3c.dom.events.MouseEvent
import util.math.Rectangle
import util.math.Vector

abstract class UIComponent(val rectangle: Rectangle? = null) {
    abstract fun render()

    open fun onPressed(mousePosition: Vector, event: MouseEvent) {

    }
}