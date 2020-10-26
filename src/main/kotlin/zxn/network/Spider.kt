package zxn.network

import util.math.Vector
import util.math.equalsNumerically
import util.math.mod
import zxn.ZXComposer
import kotlin.math.PI

class Spider(pos: Vector, val color: SpiderColor, private var _phase: Double = PI) : ZXNode(pos) {
    enum class SpiderColor(val colorRepresentation: String) {
        GREEN("#96faaa"), RED("#ff9191");
    }

    var phase: Double
        get() = _phase
        set(value) {
            _phase = value.mod(2.0*PI)
        }

    init {
        phase = _phase
    }

    override fun isPosOnElement(v: Vector): Boolean {
        return (pos - v).lengthSquared() <= ZXComposer.NODE_RADIUS
    }


    companion object {
        fun isDefaultPhase(phase: Double) = phase.equalsNumerically(PI, threshold = 0.001)
    }
}