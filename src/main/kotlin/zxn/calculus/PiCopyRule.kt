package zxn.calculus

import util.math.averagePos
import util.math.equalsNumerically
import zxn.network.Spider
import zxn.network.Wire
import zxn.network.ZXNetwork
import zxn.network.ZXNode
import kotlin.math.PI

object PiCopyRule : ZXRule("PiCopy", "K1", false, "k1.png") {
    override val inverse: ZXRule = PiCopyRuleInverse

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(!verifySpiderCount(selectedNodes, 2)) {
            return false
        }

        val neighborhoods = selectedNodes.associateWith { network.getNeighborhood(it) }
        val piSpider = selectedNodes.find {
            Spider.isPiPhase((it as Spider).phase)
                    && neighborhoods[it]!!.size == 2 && neighborhoods[it]!!.values.all { it == 1 }
        } as Spider? ?: return false

        val nonPiSpider = selectedNodes.find { it != piSpider } as Spider? ?: return false

        val piNeighborhood = neighborhoods[piSpider]!!
        val nonPiNeighborhood = neighborhoods[nonPiSpider]!!

        if(piSpider.color == nonPiSpider.color || ! nonPiSpider.phase.equalsNumerically(0.0)) {
            return false
        }

        if(piNeighborhood[nonPiSpider] != 1 || piNeighborhood.filter { it.key != nonPiSpider }.values.sum() != 1) {
            return false
        }

        if(dryRun) {
            return true
        }

        // APPLY
        // TODO: how to distribute wires
        network.removeNode(piSpider)
        network.removeNode(nonPiSpider)

        val newNonPiSpider = Spider(piSpider.pos, nonPiSpider.color)
        network.addNode(newNonPiSpider)

        val newPiSpiders = ArrayList<Spider>()
        nonPiNeighborhood.entries
                .filter { it.key != piSpider }
                .flatMap { generateSequence { it.key }.take(it.value) }
                .forEach {
                    val newSpider = Spider(averagePos(nonPiSpider.pos, it.pos), piSpider.color, _phase = PI)
                    newPiSpiders.add(newSpider)
                    network.addNode(newSpider)
                    network.addWire(Wire(newSpider, it))
                    network.addWire(Wire(newSpider, newNonPiSpider))
                }

        piNeighborhood.entries
                .filter { it.key != nonPiSpider }
                .flatMap { generateSequence { Wire(newNonPiSpider, it.key) }.take(it.value) }
                .forEach { network.addWire(it) }

        return true
    }
}

object PiCopyRuleInverse : ZXRule("PiCopy Inv", "K1_I", true, "k1i.png") {
    override val inverse: ZXRule = PiCopyRule

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(!selectedNodes.all { it is Spider } || selectedNodes.size < 2) {
            return false
        }

        val nonPiSpider = selectedNodes.find { (it as Spider).phase.equalsNumerically(0.0) } as Spider? ?: return false
        val piSpiders = selectedNodes.filter { it != nonPiSpider }.map { it as Spider }

        val neighborhoods = selectedNodes.associateWith { network.getNeighborhood(it) }

        // all pi spiders have pi phase, are connected to the copying spider, are a different color
        if(! piSpiders.all {
                    it.phase.equalsNumerically(PI)
                    && it.color != nonPiSpider.color
                    && neighborhoods[it]!![nonPiSpider] == 1
                    && neighborhoods[it]!!.filter { it.key != nonPiSpider }.values.sum() == 1
                }) {
            return false
        }

        if(neighborhoods[nonPiSpider]!!.filter { !piSpiders.contains(it.key) }.values.sum() != 1) {
            return false
        }

        val nonPiSpiderExternalNeighbor = neighborhoods[nonPiSpider]!!.keys.find { !piSpiders.contains(it) } ?: return false

        if(dryRun) {
            return true
        }

        // APPLY
        network.removeNode(nonPiSpider)
        piSpiders.forEach(network::removeNode)

        val newPiSpider = Spider(nonPiSpider.pos, nonPiSpider.color.inverse, _phase = PI)
        val newNonPiSpider = Spider(averagePos(*piSpiders.map { it.pos }.toTypedArray()), nonPiSpider.color)
        network.addNode(newPiSpider)
        network.addNode(newNonPiSpider)

        network.addWire(Wire(newPiSpider, newNonPiSpider))
        network.addWire(Wire(newPiSpider, nonPiSpiderExternalNeighbor))

        neighborhoods.entries.filter { piSpiders.contains(it.key) }
                .flatMap { it.value.entries }
                .filter { it.key != nonPiSpider }
                .flatMap { generateSequence { Wire(newNonPiSpider, it.key) }.take(it.value) }
                .forEach { network.addWire(it) }


        return true
    }
}