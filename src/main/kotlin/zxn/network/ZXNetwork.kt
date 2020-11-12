package zxn.network

import util.math.Vector
import zxn.calculus.ruleNodes.RuleNode

class ZXNetwork(val representsRule: Boolean = false) {

    val nodes: MutableList<ZXNode> = ArrayList()
    val wires: MutableList<Wire> = ArrayList()

    fun addNode(node: ZXNode) {
        check(! this.representsRule && node is RuleNode) { "This network doesn't support rule only nodes" }

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
                val multiplicity = wires.count { it.nodeSet.containsAll(nodePair) && nodePair.containsAll(it.nodeSet) }
                Pair(nodePair, multiplicity)
            }

    fun getNeighborhood(node: ZXNode): Map<ZXNode, Int> {
        if(! nodes.contains(node)) {
            return mapOf()
        }

        return getDistinctWiresWithMultiplicity().filter {
            it.first.contains(node)
        }.associateBy(
                { it.first.find { it != node } ?: node },
                { it.second }
        )
    }
}