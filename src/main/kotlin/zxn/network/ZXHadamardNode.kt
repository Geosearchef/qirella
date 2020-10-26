package zxn.network

import util.math.Rectangle
import util.math.Vector
import zxn.ZXComposer.NODE_SIZE

class ZXHadamardNode(pos: Vector) : TensorNode(pos) {

    override fun isPosOnElement(v: Vector): Boolean {
        return v in Rectangle(pos - Vector(NODE_SIZE / 2.0, NODE_SIZE / 2.0), NODE_SIZE, NODE_SIZE)
    }

    companion object {
        const val COLOR_REPRESENTATION = "#fff691"
    }
}