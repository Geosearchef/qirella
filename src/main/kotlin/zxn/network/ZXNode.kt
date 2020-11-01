package zxn.network

import util.math.Vector

abstract class ZXNode(var pos: Vector) {

    open val selectable = true
    lateinit var network: ZXNetwork // initialized when being added to the network

    operator fun contains(v: Vector) = isPosOnElement(v)
    abstract fun isPosOnElement(v: Vector): Boolean

    /**
     * returns all neighbors of this node in its network, may include the node itself
     */
    fun getNeighbors(): List<ZXNode> = network.wires.filter { it.nodeSet.contains(this) }.map { it.nodeSet.find { it != this }!! }
}