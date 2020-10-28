package ui

import org.w3c.dom.events.MouseEvent
import qcn.ui.QCAction
import rendering.Rendering
import rendering.Rendering.ctx
import rendering.color
import rendering.fillRect
import rendering.fillTextCentered
import util.math.Rectangle
import util.math.Vector

abstract class SceneUI(val width: Int, val height: Int) {

    abstract val TOP_BAR_SIZE: Double
    open val ACTION_BUTTON_COLOR = "#dddddd"

    open fun render() {
        ctx.color("#cccccc")
        ctx.fillRect(0.0, 0.0, Rendering.width, TOP_BAR_SIZE)
    }

    fun renderAction(rect: Rectangle, action: QCAction) {
        ctx.color(ACTION_BUTTON_COLOR)
        ctx.fillRect(rect.pos, rect.width, rect.height)

        ctx.color("black")
        ctx.fillTextCentered(action.representation, rect.center + Vector(y=2.0))
    }

    abstract fun onUIPressed(mousePosition: Vector, event: MouseEvent)
    abstract fun isMouseEventOnUI(mousePosition: Vector): Boolean
}