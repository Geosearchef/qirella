package zxn.network

import util.math.Vector

abstract class ZXNode(var pos: Vector) {

    open val selectable = true

    operator fun contains(v: Vector) = isPosOnElement(v)
    abstract fun isPosOnElement(v: Vector): Boolean

}