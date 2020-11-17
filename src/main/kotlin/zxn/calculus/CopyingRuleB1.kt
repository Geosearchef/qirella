package zxn.calculus

import util.math.averagePos
import util.math.equalsNumerically
import zxn.network.Spider
import zxn.network.Wire
import zxn.network.ZXNetwork
import zxn.network.ZXNode

object CopyingRuleB1 : ZXRule("Copying", "B1", false) {
    override val inverse: ZXRule = CopyingRuleB1Inverse

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(selectedNodes.size != 2 || !selectedNodes.all { it is Spider && it.phase.equalsNumerically(0.0, 0.001) }) {
            return false
        }

        val neighborhoods = selectedNodes.associateWith { network.getNeighborhood(it) }

        val copyingNode = selectedNodes.find {
            val neighborhood = neighborhoods[it]!!
            return@find neighborhood.size == 3 && neighborhood.any { it.key is Spider && it.value == 1 && selectedNodes.contains(it.key) } && neighborhood.entries.sumBy { it.value } == 3
        } as? Spider? ?: return false

        val copiedNode = selectedNodes.find {
            val neighborhood = neighborhoods[it]!!
            return@find neighborhood.size == 1 && neighborhood.containsKey(copyingNode) && neighborhood[copyingNode] == 1 && (it as Spider).color != copyingNode.color
        } as? Spider ?: return false


        val externalNode1 = neighborhoods[copyingNode]!!.keys.find { it != copyingNode && it != copiedNode } ?: return false
        val externalNode2 = neighborhoods[copyingNode]!!.keys.find { it != copyingNode && it != copiedNode && it != externalNode1 } ?: externalNode1

        if(dryRun) {
            return true
        }


        // APPLY
        network.removeNode(copyingNode)
        network.removeNode(copiedNode)
        val newNodes = listOf(Spider(copiedNode.pos, copiedNode.color), Spider(copyingNode.pos, copiedNode.color))
        newNodes.forEach(network::addNode)
        network.addWire(Wire(newNodes[0], externalNode1))
        network.addWire(Wire(newNodes[1], externalNode2))

        return true
    }


}

object CopyingRuleB1Inverse : ZXRule("CopyingInverse", "B1_I", true) {
    override val inverse: ZXRule = CopyingRuleB1

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(selectedNodes.size != 2 || !selectedNodes.all { it is Spider && it.phase.equalsNumerically(0.0, 0.001) }) {
            return false
        }

        val selectedNode1 = selectedNodes.first() as Spider
        val selectedNode2 = selectedNodes.find { it != selectedNode1 } as Spider? ?: return false

        if(selectedNode1.color != selectedNode2.color || !listOf(selectedNode1, selectedNode2).all { network.getNeighborhood(it).values.sum() == 1 }) {
            return false
        }

        val neighbor1 = network.getNeighborhood(selectedNode1).keys.firstOrNull() ?: return false
        val neighbor2 = network.getNeighborhood(selectedNode2).keys.firstOrNull() ?: return false

        if(dryRun) {
            return true
        }

        // APPLY

        network.removeNode(selectedNode1)
        network.removeNode(selectedNode2)

        val copiedNode = Spider(averagePos(selectedNode1.pos, selectedNode2.pos), selectedNode1.color)
        val copyingNode = Spider(averagePos(copiedNode.pos, averagePos(neighbor1.pos, neighbor2.pos)), selectedNode1.color.inverse)
        network.addNode(copyingNode)
        network.addNode(copiedNode)

        network.addWire(Wire(copiedNode, copyingNode))
        network.addWire(Wire(copyingNode, neighbor1))
        network.addWire(Wire(copyingNode, neighbor2))

        return true
    }

}