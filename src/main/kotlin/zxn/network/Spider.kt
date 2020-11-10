package zxn.network

import util.math.Vector
import util.math.equalsNumerically
import util.math.mod
import zxn.ZXComposer
import kotlin.math.PI
import kotlin.math.pow

class Spider(pos: Vector, var color: SpiderColor, private var _phase: Double = 0.0) : ZXNode(pos) {

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
        return (pos - v).lengthSquared() <= ZXComposer.NODE_RADIUS.pow(2)
    }

    fun toggleColor() {
        SpiderColor.values().find { it != color }?.let {
            this.color = it
        }
    }


    companion object {
        fun isDefaultPhase(phase: Double) = phase.equalsNumerically(0.0, threshold = 0.001)
    }
}