package zxn.calculus

import zxn.network.Spider
import zxn.network.Wire
import zxn.network.ZXNetwork
import zxn.network.ZXNode

object HopfLawH : ZXRule("HopfLaw", "H", false, "h.png") {

    override val inverse: ZXRule = HopfLawHInverse

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(! verifySpiderZeroPhaseCount(selectedNodes, 2)) {
            return false
        }

        val spider1 = selectedNodes[0] as Spider
        val spider2 = selectedNodes[1] as Spider

        if(spider1.color == spider2.color) {
            return false
        }

        val neighborhood1 = network.getNeighborhood(spider1)
        val neighborhood2 = network.getNeighborhood(spider2)

        if(neighborhood1.entries.filter { it.key != spider2 }.sumBy { it.value } < 1
            || neighborhood2.entries.filter { it.key != spider1 }.sumBy { it.value } < 1) {
            return false
        }

        // can be more than one node
//        val externalNode1 = neighborhood1.keys.find { it != spider2 }
//        val externalNode2 = neighborhood2.keys.find { it != spider1 }

        if(! neighborhood1.containsKey(spider2) || neighborhood1[spider2] != 2) {
            return false
        }

        if(dryRun) {
            return true
        }

        // APPLY
        network.wires.filter { it.nodeSet.containsAll(listOf(spider1, spider2)) }.forEach(network::removeWire)

        return true
    }
}

object HopfLawHInverse : ZXRule("HopfLaw Inv", "H_I", true, "hi.png") {

    override val inverse: ZXRule = HopfLawH

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(! verifySpiderZeroPhaseCount(selectedNodes, 2)) {
            return false
        }

        val spider1 = selectedNodes[0] as Spider
        val spider2 = selectedNodes[1] as Spider

        if(spider1.color == spider2.color) {
            return false
        }

        val neighborhood1 = network.getNeighborhood(spider1)
        val neighborhood2 = network.getNeighborhood(spider2)

        if(neighborhood1.values.sum() < 1 || neighborhood2.values.sum() < 1 || neighborhood1.containsKey(spider2) || neighborhood2.containsKey(spider1)) {
            return false
        }

        // count doesn't matter
//        val externalNode1 = neighborhood1.keys.firstOrNull() ?: return false
//        val externalNode2 = neighborhood2.keys.firstOrNull() ?: return false

        if(dryRun) {
            return true
        }

        // APPLY
        repeat(2) { network.addWire(Wire(spider1, spider2)) } //TODO this does sth 4 times

        return true
    }

}