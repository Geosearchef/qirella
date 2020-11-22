package zxn.calculus

import zxn.network.ZXHadamardNode
import zxn.network.ZXNetwork
import zxn.network.ZXNode

object HadamardShorthand : ZXRule("HadamardShorthand", "-", false) {
    override val inverse: ZXRule = HadamardShorthandInverse

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(selectedNodes.size != 2 || !selectedNodes.all { it is ZXHadamardNode }) {
            return false
        }

        val hadamardNode1 = selectedNodes.find { it is ZXHadamardNode } as ZXHadamardNode? ?: return false
        val hadamardNode2 = selectedNodes.find { it is ZXHadamardNode && it != hadamardNode1 } as ZXHadamardNode? ?: return false
        val neighborhood1 = network.getNeighborhood(hadamardNode1)
        val neighborhood2 = network.getNeighborhood(hadamardNode2)

        if(neighborhood1[hadamardNode2] != 1
                || neighborhood1.size != 2
                || neighborhood2.size != 2
                || neighborhood1.values.sum() > 2
                || neighborhood2.values.sum() > 2) {
            return false
        }

        val neighbor1 = neighborhood1.keys.find { it != hadamardNode2 } ?: return false
        val neighbor2 = neighborhood1.keys.find { it != hadamardNode1 } ?: return false

        if(dryRun) {
            return true
        }

        // APPLY
        network.removeNode(hadamardNode1)
        network.removeNode(hadamardNode2)

        network.addWire(neighbor1, neighbor2)

        return true
    }

}

object HadamardShorthandInverse : ZXRule("HadamardShorthand Inv", "-", true) {
    override val inverse: ZXRule = HadamardShorthand

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(selectedNodes.size != 2) {
            return false
        }

        val node1 = selectedNodes.firstOrNull() ?: return false
        val node2 = selectedNodes.find { it != node1 } ?: return false
        val neighborhood1 = network.getNeighborhood(node1)
        val neighborhood2 = network.getNeighborhood(node2)

        val wireCount = neighborhood1[node2] ?: return false

        if(wireCount == 0) {
            return false
        }

        if(dryRun) {
            return true
        }

        // APPLY
        network.wires.filter { it.nodeSet.contains(node1) && it.nodeSet.contains(node2) }.forEach(network::removeWire)
        repeat(wireCount) {
            val h1 = ZXHadamardNode(node1.pos * 0.66 + node2.pos * 0.37)
            val h2 = ZXHadamardNode(node1.pos * 0.37 + node2.pos * 0.66)
            network.addNode(h1)
            network.addNode(h2)

            network.addWire(node1, h1)
            network.addWire(h1, h2)
            network.addWire(h2, node2)
        }

        return true
    }

}