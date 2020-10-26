package zxn.network

import util.math.Vector
import kotlin.math.PI

class Spider(pos: Vector, val color: SpiderColor, val phase: Double = PI) : ZXNode(pos) {




    enum class SpiderColor {
        GREEN, RED;
    }
}