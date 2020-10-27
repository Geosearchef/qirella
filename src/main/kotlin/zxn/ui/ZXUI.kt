package zxn.ui

import org.w3c.dom.events.MouseEvent
import rendering.Rendering
import rendering.Rendering.ctx
import rendering.color
import ui.SceneUI
import util.math.Vector

class ZXUI(width: Int, height: Int) : SceneUI(width, height) {

    companion object {
        const val TOP_BAR_SIZE = 80.0

        const val ACTION_WIDTH = 50.0
        const val ACTION_HEIGHT = 25.0
        const val ACTION_SPACING = 10.0
    }


    init {

    }

    override fun onUIPressed(mousePosition: Vector, event: MouseEvent) {

    }

    override fun render() {
        ctx.color("#cccccc")
        ctx.fillRect(0.0, 0.0, Rendering.width, ZXUI.TOP_BAR_SIZE)
    }

    override fun isMouseEventOnUI(mousePosition: Vector): Boolean = mousePosition.y < TOP_BAR_SIZE
}