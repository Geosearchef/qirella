package zxn.calculus

import util.math.averagePos
import zxn.calculus.ruleNodes.ANY_NODE
import zxn.calculus.ruleNodes.RuleNodeSpider
import zxn.calculus.ruleNodes.RuleNodeSpider.RuleColor.WHITE
import zxn.calculus.ruleNodes.RuleWire
import zxn.calculus.ruleNodes.RuleWire.Companion.ANY_MULTIPLICITY
import zxn.network.Spider
import zxn.network.Wire
import zxn.network.ZXNetwork
import zxn.network.ZXNode

object SpiderRule1 : ZXRule("SpiderRule1", "S1", false) {

    override val inverse = SpiderRule1Inverse

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(selectedNodes.size != 2 || ! selectedNodes.all { it is Spider }) {
            return false
        }

        val spider1 = selectedNodes[0] as Spider
        val spider2 = selectedNodes[1] as Spider
        val color = spider1.color

        if(spider2.color != color) {
            return false
        }

        val neighborhood1 = network.getNeighborhood(spider1)
        val neighborhood2 = network.getNeighborhood(spider2)
        if(! neighborhood1.containsKey(spider2)) {
            return false
        }

        if(dryRun) {
            return true
        }

        // APPLY
        val newSpider = Spider(averagePos(spider1.pos, spider2.pos), color, spider1.phase + spider2.phase)

        network.removeNode(spider1)
        network.removeNode(spider2)
        network.addNode(newSpider)
        console.log("Before: ${network.wires.size}")
        neighborhood1.entries.union(neighborhood2.entries)
            .filter { it.key != spider1 && it.key != spider2 }
            .flatMap { generateSequence { Wire(newSpider, it.key) }.take(it.value) }
            .forEach { network.addWire(it) }
        console.log("After: ${network.wires.size}")


        return true
    }

    init {
        with(ruleNetwork) {

            val spider1 = RuleNodeSpider(WHITE, true)
            val spider2 = RuleNodeSpider(WHITE, true)

            addNode(spider1)
            addNode(spider2)
            addWire(RuleWire(spider1, spider2, ANY_MULTIPLICITY))
            addWire(RuleWire(spider1, ANY_NODE, ANY_MULTIPLICITY))
            addWire(RuleWire(spider2, ANY_NODE, ANY_MULTIPLICITY))

        }
    }

}

object SpiderRule1Inverse : ZXRule("SpiderRule1", "S1_I", true) {
    override val inverse = SpiderRule1

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        return false // TODO: not yet implemented
    }

    init {
        with(ruleNetwork) {
            val spider1 = RuleNodeSpider(WHITE, true)
            addNode(spider1)
            addWire(RuleWire(spider1, ANY_NODE, ANY_MULTIPLICITY))
        }
    }
}