package zxn.network

import util.math.Vector
import zxn.ZXComposer
import kotlin.math.pow

class QubitNode(pos: Vector, val mode: QubitNodeMode) : ZXNode(pos) {


    enum class QubitNodeMode(val representation: String) {
        INPUT("IN"), OUTPUT("OUT");
    }

    override fun isPosOnElement(v: Vector): Boolean {
        return (pos - v).lengthSquared() <= ZXComposer.QUBIT_RADIUS.pow(2)
    }
}