package zxn.calculus

import kotlinx.browser.window
import util.math.averagePos
import util.math.equalsNumerically
import zxn.network.Spider
import zxn.network.Wire
import zxn.network.ZXNetwork
import zxn.network.ZXNode

object SpiderRule2 : ZXRule("SpiderRule2", "S2", false) {

    override val inverse = SpiderRule2Inverse

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        val spider = selectedNodes.firstOrNull() as? Spider ?: return false

        val neighborhood = network.getNeighborhood(spider)

        if(! spider.phase.equalsNumerically(0.0, 0.001)) {
            return false
        }

        if(neighborhood.values.sum() != 2) { // TODO: should I enforce two neighbors?
            return false
        }

        val neighbor1 = neighborhood.keys.first()
        val neighbor2 = neighborhood.keys.find { it != neighbor1 } ?: neighbor1

        if(dryRun) {
            return true
        }

        // APPLY
        network.removeNode(spider)
        network.addWire(Wire(neighbor1, neighbor2))

        return true
    }
}

object SpiderRule2Inverse : ZXRule("SpiderRule2Inverse", "S2_I", true) {

    override val inverse = SpiderRule2


    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        val node1 = selectedNodes.firstOrNull() ?: return false
        val node2 = selectedNodes.find { it != node1 } ?: return false

        val neighborhood1 = network.getNeighborhood(node1)
        val neighborhood2 = network.getNeighborhood(node2)

        if(! neighborhood1.containsKey(node2) || ! neighborhood2.containsKey(node1)) {
            return false
        }

        val wire = network.wires.find { it.nodeSet.contains(node1) && it.nodeSet.contains(node2) } ?: return false

        if(dryRun) {
            return true
        }

        // APPLY
        val color = window.prompt(message = "Color for new node? (g/r)", default = "g")?.let {
            when(it) {
                "g", "green" -> Spider.SpiderColor.GREEN
                "r", "red" -> Spider.SpiderColor.RED
                else -> null
            }
        } ?: return false

        network.removeWire(wire)

        val newNode = Spider(averagePos(node1.pos, node2.pos), color, _phase = 0.0)
        network.addNode(newNode)
        network.addWire(Wire(node1, newNode))
        network.addWire(Wire(newNode, node2))

        return true
    }

}