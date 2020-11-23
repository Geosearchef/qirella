package zxn.calculus

import util.math.equalsNumerically
import zxn.network.Spider
import zxn.network.ZXNetwork
import zxn.network.ZXNode
import kotlin.math.PI

object PiCommutationRule : ZXRule("PiComm", "K2", false) {
    override val inverse = PiCommutationRuleInverse

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(! verifySpiderCount(selectedNodes, 2)) {
            return false
        }

        val piSpider = selectedNodes.map { it as Spider }.find { it.phase.equalsNumerically(PI, 0.01)} ?: return false
        val alphaSpider = selectedNodes.find { it != piSpider } as Spider? ?: return false
        val piNeighborhood = network.getNeighborhood(piSpider)
        val alphaNeighborhood = network.getNeighborhood(alphaSpider)

        if(piSpider.color == alphaSpider.color) {
            return false
        }

        if(piNeighborhood[alphaSpider] != 1 || alphaNeighborhood[piSpider] != 1 || piNeighborhood.values.sum() != 2 || alphaNeighborhood.values.sum() != 2) {
            return false
        }

        val externalNodePi = piNeighborhood.keys.find { it != alphaSpider } ?: return false
        val externalNodeAlpha = piNeighborhood.keys.find { it != piSpider } ?: return false

        if(dryRun) {
            return true
        }

        // APPLY

        piSpider.toggleColor()
        alphaSpider.toggleColor()

        piSpider.phase = alphaSpider.phase * (-1.0)
        alphaSpider.phase = PI

        return true
    }
}

object PiCommutationRuleInverse : ZXRule("PiComm Inv", "K2_I", true) {
    override val inverse = PiCommutationRule

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean = inverse.apply(selectedNodes, network, dryRun)

}