package zxn.calculus

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
            return neighborhood.size >= 2 && neighborhood.any { it.key is Spider && it.value == 1 } && neighborhood.entries.sumBy { it.value } == 3
        } as? Spider ?: return false

        val copiedNode = selectedNodes.find {
            val neighborhood = neighborhoods[it]!!
            return neighborhood.size == 1 && neighborhood.containsKey(copyingNode) && neighborhood[copyingNode] == 1
        } as? Spider ?: return false

        val externalNode1 = neighborhoods[copyingNode]!!.keys.first()
        val externalNode2 = neighborhoods[copyingNode]!!.keys.find { it != externalNode1 } ?: externalNode1

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
        return false
        // TODO("Not yet implemented")
    }

}