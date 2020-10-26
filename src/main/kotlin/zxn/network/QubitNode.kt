package zxn.network

import util.math.Vector
import zxn.ZXComposer

class QubitNode(pos: Vector, val mode: QubitNodeMode) : ZXNode(pos) {


    enum class QubitNodeMode(val representation: String) {
        INPUT("IN"), OUTPUT("OUT");
    }

    override fun isPosOnElement(v: Vector): Boolean {
        return (pos - v).lengthSquared() <= ZXComposer.QUBIT_RADIUS
    }
}