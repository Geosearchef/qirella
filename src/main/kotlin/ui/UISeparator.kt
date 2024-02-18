package ui

import rendering.Rendering.ctx
import rendering.color
import rendering.drawLine
import util.math.Rectangle
import util.math.Vector

class UISeparator(val pos: Vector, val size: Double, val orientation: Orientation, val color: String = "#888888") : UIComponent(
    Rectangle(pos, orientation.dir.x * size, orientation.dir.y * size)
) {

    override fun render() {
        ctx.color(color)
        ctx.drawLine(pos - orientation.dir * size / 2.0, pos + orientation.dir * size / 2.0)
    }

    enum class Orientation(val dir: Vector) {
        HORIZONTAL(Vector(x = 1.0)), VERTICAL(Vector(y = 1.0));
    }
}