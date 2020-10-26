package zxn.network

import util.math.Vector
import zxn.ZXComposer
import kotlin.math.PI

class Spider(pos: Vector, val color: SpiderColor, val phase: Double = PI) : ZXNode(pos) {

    enum class SpiderColor(val colorRepresentation: String) {
        GREEN("#96faaa"), RED("#ff9191");
    }

    override fun isPosOnElement(v: Vector): Boolean {
        return (pos - v).lengthSquared() <= ZXComposer.NODE_RADIUS
    }
}