package zxn.network

import util.math.Vector

abstract class ZXNode(var pos: Vector) {

    operator fun contains(v: Vector) = isPosOnElement(v)
    abstract fun isPosOnElement(v: Vector): Boolean

}