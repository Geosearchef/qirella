package zxn.calculus

import util.math.Vector
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

object SpiderRule1 : ZXRule("SpiderRule1", "S1", false, "s1.png") {

    override val inverse = SpiderRule1Inverse

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if((selectedNodes.size != 1 && selectedNodes.size != 2) || ! selectedNodes.all { it is Spider }) {
            return false
        }

        val spider1 = selectedNodes[0] as Spider
        val spider2 = if(selectedNodes.size == 2) selectedNodes[1] as Spider else spider1
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
        listOf(neighborhood1.entries, neighborhood2.entries).flatten()
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

object SpiderRule1Inverse : ZXRule("SpiderRule1 Inv", "S1_I", true, "s1i.png") {
    override val inverse = SpiderRule1

    override fun apply(selectedNodes: List<ZXNode>, network: ZXNetwork, dryRun: Boolean): Boolean {
        if(! verifySpiderCount(selectedNodes, 1)) {
            return false
        }

        val spider = selectedNodes.filterIsInstance<Spider>().firstOrNull() ?: return false

        if(dryRun) {
            return true
        }

        val newSpider = Spider(spider.pos + Vector(y = 60.0), spider.color)
        network.addNode(newSpider)
        network.addWire(spider, newSpider)

        return true
    }

    init {
        with(ruleNetwork) {
            val spider1 = RuleNodeSpider(WHITE, true)
            addNode(spider1)
            addWire(RuleWire(spider1, ANY_NODE, ANY_MULTIPLICITY))


        }
    }
}