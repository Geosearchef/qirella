package zxn.calculus

import util.math.Vector
import util.math.averagePos
import util.math.equalsNumerically
import zxn.network.Spider
import zxn.network.Spider.SpiderColor.GREEN
import zxn.network.Spider.SpiderColor.RED
import zxn.network.ZXHadamardNode
import zxn.network.ZXNetwork
import zxn.network.ZXNode
import kotlin.math.PI

object EulerDecomposition : ZXRule("EulerDecomp", "H", false, "euler.png") {
    override val inverse: ZXRule = EulerDecompositionInverse

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(selectedNodes.none { it is ZXHadamardNode }) {
            return false
        }

        val hadamardNodes = selectedNodes.filterIsInstance<ZXHadamardNode>()

        if(dryRun) {
            return true
        }

        // APPLY
        hadamardNodes.forEach { hadamardNode ->
            val neighbors = network.getNeighborhood(hadamardNode).keys
            network.removeNode(hadamardNode)

            val spider1 = Spider(hadamardNode.pos + Vector(x = -60.0), GREEN, _phase = PI / 2.0).also(network::addNode)
            val spider2 = Spider(hadamardNode.pos + Vector(x =   0.0),   RED, _phase = PI / 2.0).also(network::addNode)
            val spider3 = Spider(hadamardNode.pos + Vector(x = +60.0), GREEN, _phase = PI / 2.0).also(network::addNode)

            network.addWire(spider1, spider2)
            network.addWire(spider2, spider3)
            val n1 = neighbors.firstOrNull()
            val n2 = neighbors.find { it != n1 }

            n1?.let { network.addWire(it, spider1) }
            n2?.let { network.addWire(it, spider3) }
        }

        return true
    }

}

object EulerDecompositionInverse : ZXRule("EulerDecomp Inv", "H_I", true, "eulerI.png") {
    override val inverse: ZXRule = EulerDecomposition

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(!verifySpiderCount(selectedNodes, 3)) {
            return false
        }

        val greenSpider1 = selectedNodes.find { it is Spider && it.color == GREEN && it.phase.equalsNumerically(0.5*PI) } as Spider? ?: return false
        val redSpider = selectedNodes.find { it is Spider && it.color == RED && it.phase.equalsNumerically(0.5*PI) } as Spider? ?: return false
        val greenSpider2 = selectedNodes.find { it is Spider && it.color == GREEN && it != greenSpider1 && it.phase.equalsNumerically(0.5*PI) } as Spider? ?: return false

        val neighborhoods = selectedNodes.associateWith(network::getNeighborhood)

        if(neighborhoods[redSpider]!!.keys.filter { it != greenSpider1 && it != greenSpider2 }.isNotEmpty()
                || neighborhoods[redSpider]!![greenSpider1] != 1 || neighborhoods[redSpider]!![greenSpider2] != 1) {
            return false
        }

        if(neighborhoods[greenSpider1]!![redSpider] != 1 || neighborhoods[greenSpider2]!![redSpider] != 1) {
            return false
        }

        // only accept a single neighbor on each side as hadamard nodes can only have two neighbors
        if(neighborhoods[greenSpider1]!!.filter { it.key != redSpider }.values.sum() > 1
                || neighborhoods[greenSpider1]!!.filter { it.key != redSpider }.values.sum() > 1) {
            return false
        }

        val neighbor1: ZXNode? = neighborhoods[greenSpider1]?.keys?.find { it != redSpider }
        val neighbor2: ZXNode? = neighborhoods[greenSpider2]?.keys?.find { it != redSpider }

        if(dryRun) {
            return true
        }

        // APPLY
        with(network) {
            removeNode(greenSpider1)
            removeNode(redSpider)
            removeNode(greenSpider2)

            val hadamardNode = ZXHadamardNode(averagePos(greenSpider1.pos, redSpider.pos, greenSpider2.pos))
            addNode(hadamardNode)
            neighbor1?.let { addWire(hadamardNode, it) }
            neighbor2?.let { addWire(hadamardNode, it) }
        }

        return true
    }

}