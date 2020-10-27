package zxn.network

import util.math.Vector

class ZXNetwork {

    val nodes: MutableList<ZXNode> = ArrayList()
    val wires: MutableList<Wire> = ArrayList()


    fun addNode(node: ZXNode) {
        nodes.add(node)
    }

    fun removeNode(node: ZXNode) {
        nodes.remove(node)
    }

    fun addWire(wire: Wire) {
        wires.add(wire)
    }

    fun removeWire(wire: Wire) {
        wires.remove(wire)
    }

    fun setNodePosition(node: ZXNode, newPos: Vector) {
        node.pos = newPos
    }
}