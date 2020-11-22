package zxn.calculus

import util.math.averagePos
import zxn.network.*

object ColorRule : ZXRule("ColorRule", "C", false) {
    override val inverse: ZXRule = ColorRuleInverse

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(!verifySpiderCount(selectedNodes, 1)) {
            return false
        }

        val spider = selectedNodes.first() as Spider
        val neighborhood = network.getNeighborhood(spider)

//        val allNeighborsHadamard = neighborhood.all { it.key is ZXHadamardNode }

        if(dryRun) {
            return true
        }

        // APPLY
        spider.toggleColor()

        network.wires.filter { it.nodeSet.contains(spider) }.forEach(network::removeWire)

        neighborhood.entries
                .flatMap { generateSequence { it.key }.take(it.value) }
                .forEach {
                    val hadamardNode = ZXHadamardNode(averagePos(spider.pos, it.pos))
                    network.addNode(hadamardNode)

                    network.addWire(Wire(hadamardNode, spider))
                    network.addWire(Wire(hadamardNode, it))
                }

        return true
    }
}

object ColorRuleInverse : ZXRule("ColorRule Inv", "C_I", true) {
    override val inverse: ZXRule = ColorRule

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean) = ColorRule.apply(selectedNodes, network, dryRun)
}