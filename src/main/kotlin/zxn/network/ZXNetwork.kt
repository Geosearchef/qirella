package zxn.network

import util.math.Vector

class ZXNetwork {

    val nodes: MutableList<ZXNode> = ArrayList()
    val wires: MutableList<Wire> = ArrayList()

    fun addNode(node: ZXNode) {
        node.network = this
        nodes.add(node)
    }

    fun removeNode(node: ZXNode) {
        nodes.remove(node)
        wires.filter { it.nodes.first == node || it.nodes.second == node }.forEach { removeWire(it) }
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

    fun getDistinctWiresWithMultiplicity(): List<Pair<List<ZXNode>, Int>> =
        wires
            .map { it.nodeSet }
            .distinct()
            .map { ArrayList(it) }.map { nodePair ->
                val multiplicity = wires.count { it.nodeSet.containsAll(nodePair) }
                Pair(nodePair, multiplicity)
            }
}