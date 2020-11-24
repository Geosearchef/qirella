package zxn.calculus

import util.math.Vector
import util.math.averagePos
import zxn.network.Spider
import zxn.network.Spider.SpiderColor.GREEN
import zxn.network.Spider.SpiderColor.RED
import zxn.network.Wire
import zxn.network.ZXNetwork
import zxn.network.ZXNode

object BialgebraLawB2 : ZXRule("BialgebraLaw", "B2", false, "b2.png") {

    override val inverse: ZXRule = BialgebraLawB2Inverse

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(! verifySpiderZeroPhaseCount(selectedNodes, 4)) {
            return false
        }

        val greenSpiders = selectedNodes.spiders(GREEN)
        val redSpiders = selectedNodes.spiders(RED)
        val allSpiders = greenSpiders.union(redSpiders)

        val neighborhoods = selectedNodes.associateWith(network::getNeighborhood)

        if(greenSpiders.size != 2 || redSpiders.size != 2) {
            return false
        }

        // neighbored to all nodes of other color
        if(! greenSpiders.all { green -> redSpiders.all { red -> neighborhoods[green]!![red] == 1 } }
                || ! redSpiders.all { red -> greenSpiders.all { green -> neighborhoods[red]!![green] == 1 } }) {
            return false
        }

        // not neighbored to node of own color or self
        if(greenSpiders.any { g1 -> greenSpiders.any { g2 -> neighborhoods[g1]!!.containsKey(g2) } }
                || redSpiders.any { r1 -> redSpiders.any { r2 -> neighborhoods[r1]!!.containsKey(r2) } } ) {
            return false
        }

        // any node hasn't gotten 3 wires
        if(allSpiders.any { neighborhoods[it]!!.values.sum() != 3 }) {

        }

        val externalNodeBySpider = greenSpiders.union(redSpiders).associateWith {
            neighborhoods[it]!!.keys.find { !allSpiders.contains(it) } ?: return@apply false
        }

        if(dryRun) {
            return true
        }

        // APPLY

        allSpiders.forEach(network::removeNode)

        val newRedSpider = Spider(averagePos(*(greenSpiders.map { it.pos }.toTypedArray())), RED)
        val newGreenSpider = Spider(averagePos(*(redSpiders.map { it.pos }.toTypedArray())), GREEN)
        network.addNode(newGreenSpider)
        network.addNode(newRedSpider)
        network.addWire(Wire(newGreenSpider, newRedSpider))

        redSpiders.map { externalNodeBySpider[it]!! }.forEach { network.addWire(Wire(newGreenSpider, it)) }
        greenSpiders.map { externalNodeBySpider[it]!! }.forEach { network.addWire(Wire(newRedSpider, it)) }

        return true
    }
}

object BialgebraLawB2Inverse : ZXRule("BialgebraLaw Inv", "B2_I", true, "b2i.png") {

    override val inverse: ZXRule = BialgebraLawB2

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if (!verifySpiderZeroPhaseCount(selectedNodes, 2)) {
            return false
        }

        val greenSpider = selectedNodes.spiders().find { it.color == GREEN } ?: return false
        val redSpider = selectedNodes.spiders().find { it.color == RED } ?: return false
        val greenNeighborhood = network.getNeighborhood(greenSpider)
        val redNeighborhood = network.getNeighborhood(redSpider)

        if (redNeighborhood[greenSpider] != 1 || greenNeighborhood[redSpider] != 1) {
            return false
        }

        if (greenNeighborhood.containsKey(greenSpider) || redNeighborhood.containsKey(redSpider)) {
            return false
        }

        if (greenNeighborhood.values.sum() != 3 || redNeighborhood.values.sum() != 3) {
            return false
        }

        val greenNeighbors = greenNeighborhood.keys.filter { it != redSpider }.toMutableList()
        val redNeighbors = redNeighborhood.keys.filter { it != greenSpider }.toMutableList()

        if(greenNeighbors.size < 2) {
            greenNeighbors.add(greenNeighbors.firstOrNull() ?: return false)
        }
        if(redNeighbors.size < 2) {
            redNeighbors.add(redNeighbors.firstOrNull() ?: return false)
        }


        if (dryRun) {
            return true
        }

        // APPLY

        network.removeNode(greenSpider)
        network.removeNode(redSpider)

        val newGreenSpiders = listOf(
                Spider(redSpider.pos - Vector(x = -30.0), GREEN),
                Spider(redSpider.pos - Vector(x = 30.0), GREEN)
        )

        val newRedSpiders = listOf(
                Spider(greenSpider.pos - Vector(x = 30.0), RED),
                Spider(greenSpider.pos - Vector(x = -30.0), RED)
        )

        val allNewSpiders = newGreenSpiders.union(newRedSpiders)
        allNewSpiders.forEach(network::addNode)

        newGreenSpiders.forEach { g -> newRedSpiders.forEach { r -> network.addWire(Wire(g, r)) } }

        network.addWire(Wire(redNeighbors[0], newGreenSpiders[0]))
        network.addWire(Wire(redNeighbors[1], newGreenSpiders[1]))
        network.addWire(Wire(greenNeighbors[0], newRedSpiders[0]))
        network.addWire(Wire(greenNeighbors[1], newRedSpiders[1]))

        return true
    }
}