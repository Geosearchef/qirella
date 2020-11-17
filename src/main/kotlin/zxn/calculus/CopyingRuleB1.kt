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
        console.log("trying B1 rule")
        val neighborhoods = selectedNodes.associateWith { network.getNeighborhood(it) }


//        val copiedNode = selectedNodes.find {
//            val neighborhood = network.getNeighborhood(it)
//            return@find true
////            return neighborhood.size == 1 && neighborhood.values.sum() == 1
//        } as Spider? ?: return false
//
//        console.log("found individual copied node")

//        val copyingNode = copiedNode

        val copyingNode = selectedNodes.find {
            val neighborhood = neighborhoods[it]!!
            console.log("size: ${neighborhood.size}, has singleConnNeighborSpider selected: ${neighborhood.any { it.key is Spider && it.value == 1 && selectedNodes.contains(it) }}, neighborhoodConnsSum: ${neighborhood.entries.sumBy { it.value }}, returning: ${neighborhood.size >= 2 && neighborhood.any { it.key is Spider && it.value == 1 && selectedNodes.contains(it) } && neighborhood.entries.sumBy { it.value } == 3}")
            return@find neighborhood.size == 3 && neighborhood.any { it.key is Spider && it.value == 1 && selectedNodes.contains(it.key) } && neighborhood.entries.sumBy { it.value } == 3
        } as? Spider? ?: return false

        console.log("found copying node: " + copyingNode.color)

        val copiedNode = selectedNodes.find {
            val neighborhood = neighborhoods[it]!!
            return@find neighborhood.size == 1 && neighborhood.containsKey(copyingNode) && neighborhood[copyingNode] == 1 && (it as Spider).color != copyingNode.color
        } as? Spider ?: return false

        console.log("found copied node: " + copiedNode.color)

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
        return false
        // TODO("Not yet implemented")
    }

}