package zxn.network

import util.math.Vector
import util.math.equalsNumerically
import util.math.mod
import zxn.ZXComposer
import kotlin.math.PI
import kotlin.math.pow

class Spider(pos: Vector, var color: SpiderColor, private var _phase: Double = 0.0) : ZXNode(pos) {

    enum class SpiderColor(val _colorRepresentation: String, val greyscaleRepresentation: String) {
        GREEN("#96faaa", "#eeeeee"), RED("#ff9191", "#666666");

        val colorRepresentation get() = if(ZXComposer.greyscaleMode) greyscaleRepresentation else _colorRepresentation

        val inverse get() = SpiderColor.values().find { it != this }!!
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
        fun isPiPhase(phase: Double) = phase.equalsNumerically(PI, threshold = 0.001)
    }
}