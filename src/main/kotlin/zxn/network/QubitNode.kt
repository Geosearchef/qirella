package zxn.network

import util.math.Vector

class QubitNode(pos: Vector, val mode: QubitNodeMode) : ZXNode(pos) {


    enum class QubitNodeMode {
        INPUT, OUTPUT;
    }
}